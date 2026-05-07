package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.dto.AgendamentoRespostaDto;
import estopa.agenda_facil.model.dto.AgendamentoSolicitarDto;
import estopa.agenda_facil.model.entity.Agendamento;
import estopa.agenda_facil.model.entity.Cliente;
import estopa.agenda_facil.model.entity.Estabelecimento;
import estopa.agenda_facil.model.enums.StatusAgendamento;
import estopa.agenda_facil.model.enums.StatusPagamento;
import estopa.agenda_facil.model.repository.AgendamentoRepository;
import estopa.agenda_facil.model.repository.ClienteRepository;
import estopa.agenda_facil.model.repository.EstabelecimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public AgendamentoRespostaDto solicitarAgendamento(Long idCliente, AgendamentoSolicitarDto dto) {

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado."));

        Estabelecimento estabelecimento = estabelecimentoRepository.findById(dto.estabelecimentoId())
                .orElseThrow(() -> new RegraNegocioException("Estabelecimento não encontrado."));

        StatusAgendamento statusInicial = estabelecimento.isAprovacaoAutomatica()
                ? StatusAgendamento.CONFIRMADO
                : StatusAgendamento.AGUARDANDO_CONFIRMACAO;

        Agendamento agendamento = new Agendamento(
                dto.dataHora(),
                dto.formaPagamento(),
                statusInicial,
                StatusPagamento.AGUARDANDO_PAGAMENTO,
                cliente,
                estabelecimento
        );

        agendamentoRepository.save(agendamento);

        return new AgendamentoRespostaDto(
                agendamento.getId(),
                agendamento.getDataHora(),
                estabelecimento.getNome(),
                estabelecimento.getDescricaoEspecialidade(),
                agendamento.getStatusAgendamento(),
                agendamento.getStatusPagamento()
        );
    }

    @Transactional
    public void confirmarAgendamento(Long idAgendamento) {
        agendamentoRepository.atualizarStatusAgendamento(idAgendamento, StatusAgendamento.CONFIRMADO);
    }

    @Transactional
    public void registrarPagamento(Long idAgendamento) {
        agendamentoRepository.atualizarStatusPagamento(idAgendamento, StatusPagamento.PAGO);
    }

    @Transactional
    public void cancelarAgendamento(Long idAgendamento) {
        agendamentoRepository.atualizarStatusAgendamento(idAgendamento, StatusAgendamento.CANCELADO_CLIENTE);
    }
}