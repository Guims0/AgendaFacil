package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.entity.Agendamento;
import estopa.agenda_facil.model.entity.Carteira;
import estopa.agenda_facil.model.enums.FormaPagamento;
import estopa.agenda_facil.model.enums.StatusPagamento;
import estopa.agenda_facil.model.repository.AgendamentoRepository;
import estopa.agenda_facil.model.repository.CarteiraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final CarteiraRepository carteiraRepository;

    private static final double TAXA_USO_CARTEIRA = 0.05;


    @Transactional
    public void pagarOnlineComCarteira(Long idAgendamento, double valorDoServico) {

        Agendamento agendamento = buscarAgendamentoPendente(idAgendamento);

        if (agendamento.getFormaPagamento() != FormaPagamento.CARTEIRA) {
            throw new RegraNegocioException("Este agendamento foi configurado para pagamento no local.");
        }

        Long idUsuarioCliente = agendamento.getCliente().getId();
        Long idUsuarioEstabelecimento = agendamento.getEstabelecimento().getId();

        Carteira carteiraCliente = carteiraRepository.findByUsuarioId(idUsuarioCliente)
                .orElseThrow(() -> new RegraNegocioException("Carteira do cliente não encontrada."));

        if (carteiraCliente.getSaldo() < valorDoServico) {
            throw new RegraNegocioException("Saldo insuficiente na carteira para realizar o pagamento.");
        }


        double valorTaxa = valorDoServico * TAXA_USO_CARTEIRA;
        double valorRepasse = valorDoServico - valorTaxa;

        carteiraRepository.debitarSaldo(carteiraCliente.getId(), valorDoServico);

        Carteira carteiraEstab = carteiraRepository.findByUsuarioId(idUsuarioEstabelecimento)
                .orElseThrow(() -> new RegraNegocioException("Carteira do estabelecimento não encontrada."));

        carteiraRepository.adicionarSaldo(carteiraEstab.getId(), valorRepasse);


        agendamentoRepository.atualizarStatusPagamento(idAgendamento, StatusPagamento.PAGO);
    }


    @Transactional
    public void confirmarPagamentoNoLocal(Long idAgendamento) {

        Agendamento agendamento = buscarAgendamentoPendente(idAgendamento);

        if (agendamento.getFormaPagamento() != FormaPagamento.LOCAL) {
            throw new RegraNegocioException("Este agendamento é online e não pode ser confirmado manualmente.");
        }


        agendamentoRepository.atualizarStatusPagamento(idAgendamento, StatusPagamento.PAGO);
    }


    @Transactional
    public void cobrarMensalidade(Long idEstabelecimento, double valorMensalidade) {
        Carteira carteiraEstab = carteiraRepository.findByUsuarioId(idEstabelecimento)
                .orElseThrow(() -> new RegraNegocioException("Carteira do estabelecimento não encontrada."));

        carteiraRepository.debitarSaldo(carteiraEstab.getId(), valorMensalidade);
    }


    private Agendamento buscarAgendamentoPendente(Long idAgendamento) {
        Agendamento agendamento = agendamentoRepository.findById(idAgendamento)
                .orElseThrow(() -> new RegraNegocioException("Agendamento não encontrado."));

        if (agendamento.getStatusPagamento() == StatusPagamento.PAGO) {
            throw new RegraNegocioException("Este agendamento já foi pago.");
        }
        return agendamento;
    }

    @Transactional
    public void estornarPagamentoOnline(Long idAgendamento, Double valorDoServico, Long idCliente, Long idEstabelecimento) {

        Carteira carteiraCliente = carteiraRepository.findByUsuarioId(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Carteira do cliente não encontrada."));

        Carteira carteiraEstab = carteiraRepository.findByUsuarioId(idEstabelecimento)
                .orElseThrow(() -> new RegraNegocioException("Carteira do estabelecimento não encontrada."));

        Double taxaPlataforma = valorDoServico * TAXA_USO_CARTEIRA;
        Double valorLiquidoEstabelecimento = valorDoServico - taxaPlataforma;

        carteiraRepository.adicionarSaldo(carteiraCliente.getId(), valorDoServico);

        carteiraRepository.debitarSaldo(carteiraEstab.getId(), valorLiquidoEstabelecimento);
    }
}