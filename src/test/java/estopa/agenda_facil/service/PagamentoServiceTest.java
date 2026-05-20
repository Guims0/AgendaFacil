package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.entity.*;
import estopa.agenda_facil.model.enums.FormaPagamento;
import estopa.agenda_facil.model.enums.StatusAgendamento;
import estopa.agenda_facil.model.enums.StatusPagamento;
import estopa.agenda_facil.model.repository.AgendamentoRepository;
import estopa.agenda_facil.model.repository.CarteiraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @InjectMocks
    private PagamentoService pagamentoService;

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private CarteiraRepository carteiraRepository;

    private Cliente cliente;
    private Estabelecimento estabelecimento;
    private Servico servico;
    private Agendamento agendamentoOnline;
    private Carteira carteiraCliente;
    private Carteira carteiraEstabelecimento;

    @BeforeEach
    void setup() {
        cliente = new Cliente("João", "joao@email.com", "123", "111", "999");
        ReflectionTestUtils.setField(cliente, "id", 1L);

        estabelecimento = new Estabelecimento("Barbearia", "barb@email.com", "123", "222", 30, "Cortes", true);
        ReflectionTestUtils.setField(estabelecimento, "id", 2L);

        servico = new Servico("Corte", "Corte simples", 100.0, estabelecimento);

        agendamentoOnline = new Agendamento(LocalDateTime.now().plusDays(1), FormaPagamento.CARTEIRA, StatusAgendamento.CONFIRMADO, StatusPagamento.AGUARDANDO_PAGAMENTO, cliente, estabelecimento, servico);
        ReflectionTestUtils.setField(agendamentoOnline, "id", 10L);

        carteiraCliente = new Carteira(150.0, cliente); // Saldo suficiente (150 > 100)
        ReflectionTestUtils.setField(carteiraCliente, "id", 100L);

        carteiraEstabelecimento = new Carteira(500.0, estabelecimento);
        ReflectionTestUtils.setField(carteiraEstabelecimento, "id", 200L);
    }

    @Test
    @DisplayName("Financeiro: Pagar com carteira com sucesso e cobrar taxa de 5%")
    void devePagarOnlineERepassarValorComTaxa() {
        when(agendamentoRepository.findById(10L)).thenReturn(Optional.of(agendamentoOnline));
        when(carteiraRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carteiraCliente));
        when(carteiraRepository.findByUsuarioId(2L)).thenReturn(Optional.of(carteiraEstabelecimento));

        pagamentoService.pagarOnlineComCarteira(10L, servico.getPreco());

        verify(carteiraRepository, times(1)).debitarSaldo(100L, 100.0);
        verify(carteiraRepository, times(1)).adicionarSaldo(200L, 95.0);
        verify(agendamentoRepository, times(1)).atualizarStatusPagamento(10L, StatusPagamento.PAGO);
    }

    @Test
    @DisplayName("Financeiro: Impedir pagamento se saldo for menor que o valor do serviço")
    void deveLancarErroQuandoSaldoInsuficiente() {
        ReflectionTestUtils.setField(carteiraCliente, "saldo", 20.0);

        when(agendamentoRepository.findById(10L)).thenReturn(Optional.of(agendamentoOnline));
        when(carteiraRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carteiraCliente));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class,
                () -> pagamentoService.pagarOnlineComCarteira(10L, servico.getPreco()));

        assertEquals("Saldo insuficiente na carteira para realizar o pagamento.", exception.getMessage());

        verify(carteiraRepository, never()).debitarSaldo(anyLong(), anyDouble());
        verify(carteiraRepository, never()).adicionarSaldo(anyLong(), anyDouble());
    }

    @Test
    @DisplayName("Financeiro: Estorno perfeito devolvendo R$100 pro Cliente e tirando os R$95 do Estabelecimento")
    void deveEstornarPagamentoPerfeitamente() {
        when(carteiraRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carteiraCliente));
        when(carteiraRepository.findByUsuarioId(2L)).thenReturn(Optional.of(carteiraEstabelecimento));

        pagamentoService.estornarPagamentoOnline(10L, 100.0, 1L, 2L);

        verify(carteiraRepository, times(1)).adicionarSaldo(100L, 100.0);
        verify(carteiraRepository, times(1)).debitarSaldo(200L, 95.0);
    }
}