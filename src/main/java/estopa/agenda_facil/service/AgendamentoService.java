package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.dto.AgendamentoRespostaDto;
import estopa.agenda_facil.model.dto.AgendamentoSolicitarDto;
import estopa.agenda_facil.model.entity.*;
import estopa.agenda_facil.model.enums.FormaPagamento;
import estopa.agenda_facil.model.enums.StatusAgendamento;
import estopa.agenda_facil.model.enums.StatusPagamento;
import estopa.agenda_facil.model.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;
    private final ExpedienteRepository expedienteRepository;
    private final PagamentoService pagamentoService;

    @Transactional
    public AgendamentoRespostaDto solicitarAgendamento(Usuario usuarioLogado, AgendamentoSolicitarDto dto) {

        Long idCliente;
        Long idEstabelecimento;

        if (usuarioLogado.getRole() == estopa.agenda_facil.model.enums.RoleUsuario.ESTABELECIMENTO) {
            idEstabelecimento = usuarioLogado.getId();
            idCliente = dto.clienteId();
            if (idCliente == null) {
                throw new RegraNegocioException("O ID do cliente é obrigatório quando o estabelecimento agenda um horário.");
            }
        } else {
            idCliente = usuarioLogado.getId();
            idEstabelecimento = dto.estabelecimentoId();
            if (idEstabelecimento == null) {
                throw new RegraNegocioException("O ID do estabelecimento é obrigatório.");
            }
        }

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado."));

        Estabelecimento estabelecimento = estabelecimentoRepository.findById(idEstabelecimento)
                .orElseThrow(() -> new RegraNegocioException("Estabelecimento não encontrado."));

        if (!estabelecimento.isEnabled()) {
            throw new RegraNegocioException("Este estabelecimento não está mais disponível.");
        }

        List<Expediente> expedientes = expedienteRepository.findByEstabelecimentoId(estabelecimento.getId());
        if (expedientes.isEmpty()) {
            throw new RegraNegocioException("O estabelecimento ainda não configurou seu quadro de horários.");
        }

        java.time.DayOfWeek diaAgendamento = dto.dataHora().getDayOfWeek();
        java.time.LocalTime horaAgendamento = dto.dataHora().toLocalTime();

        boolean dentroDoExpediente = expedientes.stream()
                .anyMatch(exp -> exp.getDiaDaSemana().equals(diaAgendamento) &&
                        !horaAgendamento.isBefore(exp.getHoraInicio()) &&
                        !horaAgendamento.isAfter(exp.getHoraFim()));

        if (!dentroDoExpediente) {
            throw new RegraNegocioException("O horário selecionado está fora do expediente de funcionamento.");
        }

        var statusOcupados = List.of(StatusAgendamento.CONFIRMADO, StatusAgendamento.AGUARDANDO_CONFIRMACAO);
        boolean horarioOcupado = agendamentoRepository.existsByEstabelecimentoIdAndDataHoraAndStatusAgendamentoIn(
                estabelecimento.getId(), dto.dataHora(), statusOcupados
        );

        if (horarioOcupado) {
            throw new RegraNegocioException("Este horário já está reservado. Por favor, escolha outro.");
        }

        Servico servico = servicoRepository.findById(dto.servicoId())
                .orElseThrow(() -> new RegraNegocioException("Serviço não encontrado."));

        if (!servico.getEstabelecimento().getId().equals(estabelecimento.getId())) {
            throw new RegraNegocioException("O serviço selecionado não pertence a este estabelecimento.");
        }

        StatusAgendamento statusInicial;
        if (usuarioLogado.getRole() == estopa.agenda_facil.model.enums.RoleUsuario.ESTABELECIMENTO) {
            statusInicial = StatusAgendamento.CONFIRMADO;
        } else {
            statusInicial = estabelecimento.isAprovacaoAutomatica() ? StatusAgendamento.CONFIRMADO : StatusAgendamento.AGUARDANDO_CONFIRMACAO;
        }

        Agendamento agendamento = new Agendamento(
                dto.dataHora(),
                dto.formaPagamento(),
                statusInicial,
                StatusPagamento.AGUARDANDO_PAGAMENTO,
                cliente,
                estabelecimento,
                servico
        );

        agendamento = agendamentoRepository.save(agendamento);

        StatusPagamento statusPagamentoFinal = StatusPagamento.AGUARDANDO_PAGAMENTO;

        if (dto.formaPagamento() == FormaPagamento.CARTEIRA) {
            pagamentoService.pagarOnlineComCarteira(agendamento.getId(), servico.getPreco());
            statusPagamentoFinal = StatusPagamento.PAGO;
        }

        return mapearParaResposta(agendamento, statusPagamentoFinal);
    }


    public List<AgendamentoRespostaDto> listarPorCliente(Long idCliente) {
        return agendamentoRepository.findByClienteId(idCliente).stream()
                .map(a -> mapearParaResposta(a, a.getStatusPagamento()))
                .toList();
    }

    public List<AgendamentoRespostaDto> listarPorEstabelecimento(Long idEstabelecimento) {
        return agendamentoRepository.findByEstabelecimentoId(idEstabelecimento).stream()
                .map(a -> mapearParaResposta(a, a.getStatusPagamento()))
                .toList();
    }

    @Transactional
    public void confirmarAgendamento(Long idAgendamento, Long idEstabelecimentoLogado) {
        Agendamento agendamento = agendamentoRepository.findById(idAgendamento)
                .orElseThrow(() -> new RegraNegocioException("Agendamento não encontrado."));

        if (!agendamento.getEstabelecimento().getId().equals(idEstabelecimentoLogado)) {
            throw new RegraNegocioException("Este agendamento não pertence ao seu estabelecimento.");
        }
        agendamentoRepository.atualizarStatusAgendamento(idAgendamento, StatusAgendamento.CONFIRMADO);
    }

    @Transactional
    public void registrarPagamento(Long idAgendamento, Long idEstabelecimentoLogado) {
        Agendamento agendamento = agendamentoRepository.findById(idAgendamento)
                .orElseThrow(() -> new RegraNegocioException("Agendamento não encontrado."));

        if (!agendamento.getEstabelecimento().getId().equals(idEstabelecimentoLogado)) {
            throw new RegraNegocioException("Este agendamento não pertence ao seu estabelecimento.");
        }
        agendamentoRepository.atualizarStatusPagamento(idAgendamento, StatusPagamento.PAGO);
    }

    @Transactional
    public void cancelarAgendamento(Long idAgendamento, Long idUsuarioLogado) {
        Agendamento agendamento = agendamentoRepository.findById(idAgendamento)
                .orElseThrow(() -> new RegraNegocioException("Agendamento não encontrado."));

        boolean isCliente = agendamento.getCliente().getId().equals(idUsuarioLogado);
        boolean isEstabelecimento = agendamento.getEstabelecimento().getId().equals(idUsuarioLogado);

        if (!isCliente && !isEstabelecimento) {
            throw new RegraNegocioException("Você não tem permissão para cancelar este agendamento.");
        }

        if (agendamento.getStatusAgendamento() == StatusAgendamento.CANCELADO_CLIENTE ||
                agendamento.getStatusAgendamento() == StatusAgendamento.CANCELADO_ESTABELECIMENTO) {
            throw new RegraNegocioException("Este agendamento já encontra-se cancelado.");
        }

        if (agendamento.getStatusAgendamento() == StatusAgendamento.FINALIZADO ||
                agendamento.getStatusAgendamento() == StatusAgendamento.FALTA_CLIENTE) {
            throw new RegraNegocioException("Não é possível cancelar um agendamento já encerrado ou com falta registrada.");
        }

        if (agendamento.getStatusPagamento() == StatusPagamento.PAGO) {
            pagamentoService.estornarPagamentoOnline(
                    agendamento.getId(), agendamento.getServico().getPreco(),
                    agendamento.getCliente().getId(), agendamento.getEstabelecimento().getId()
            );
            agendamentoRepository.atualizarStatusPagamento(idAgendamento, StatusPagamento.ESTORNADO);
        }

        StatusAgendamento novoStatus = isCliente ? StatusAgendamento.CANCELADO_CLIENTE : StatusAgendamento.CANCELADO_ESTABELECIMENTO;
        agendamentoRepository.atualizarStatusAgendamento(idAgendamento, novoStatus);
    }

    private AgendamentoRespostaDto mapearParaResposta(Agendamento agendamento, StatusPagamento statusPagamentoFinal) {
        return new AgendamentoRespostaDto(
                agendamento.getId(), agendamento.getDataHora(),
                agendamento.getEstabelecimento().getNome(),
                agendamento.getEstabelecimento().getDescricaoEspecialidade(),
                agendamento.getStatusAgendamento(), statusPagamentoFinal
        );
    }
}