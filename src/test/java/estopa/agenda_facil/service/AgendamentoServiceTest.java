package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.dto.AgendamentoRespostaDto;
import estopa.agenda_facil.model.dto.AgendamentoSolicitarDto;
import estopa.agenda_facil.model.entity.*;
import estopa.agenda_facil.model.enums.FormaPagamento;
import estopa.agenda_facil.model.enums.StatusAgendamento;
import estopa.agenda_facil.model.enums.StatusPagamento;
import estopa.agenda_facil.model.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @InjectMocks
    private AgendamentoService agendamentoService;

    @Mock private AgendamentoRepository agendamentoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private EstabelecimentoRepository estabelecimentoRepository;
    @Mock private ServicoRepository servicoRepository;
    @Mock private ExpedienteRepository expedienteRepository;
    @Mock private PagamentoService pagamentoService;

    private Cliente cliente;
    private Estabelecimento estabelecimento;
    private Servico servico;
    private Expediente expediente;
    private LocalDateTime dataHoraValida;

    @BeforeEach
    void setup() {
        cliente = new Cliente("João", "joao@email.com", "123", "11122233344", "999999999");
        ReflectionTestUtils.setField(cliente, "id", 1L);

        estabelecimento = new Estabelecimento("Barbearia", "barb@email.com", "123", "12345678000195", 30, "Cortes", true);
        ReflectionTestUtils.setField(estabelecimento, "id", 2L);

        servico = new Servico("Corte", "Corte simples", 35.0, estabelecimento);
        ReflectionTestUtils.setField(servico, "id", 3L);

        expediente = new Expediente(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(18, 0), estabelecimento);

        dataHoraValida = LocalDateTime.of(2026, 5, 20, 10, 0);
    }

    @Test
    @DisplayName("Caminho Feliz: Cliente agendando pelo App")
    void deveAgendarComSucessoQuandoClienteSolicita() {
        AgendamentoSolicitarDto dto = new AgendamentoSolicitarDto(2L, null, 3L, dataHoraValida, FormaPagamento.LOCAL);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(estabelecimentoRepository.findById(2L)).thenReturn(Optional.of(estabelecimento));
        when(expedienteRepository.findByEstabelecimentoId(2L)).thenReturn(List.of(expediente));
        when(agendamentoRepository.existsByEstabelecimentoIdAndDataHoraAndStatusAgendamentoIn(any(), any(), any())).thenReturn(false);
        when(servicoRepository.findById(3L)).thenReturn(Optional.of(servico));

        Agendamento agendamentoSalvo = new Agendamento(dto.dataHora(), dto.formaPagamento(), StatusAgendamento.CONFIRMADO, StatusPagamento.AGUARDANDO_PAGAMENTO, cliente, estabelecimento, servico);
        ReflectionTestUtils.setField(agendamentoSalvo, "id", 10L);
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoSalvo);

        AgendamentoRespostaDto resposta = agendamentoService.solicitarAgendamento(cliente, dto);

        assertNotNull(resposta);
        assertEquals(10L, resposta.idAgendamento());
        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Caminho Feliz: Recepcionista agendando para o Cliente (Status automático CONFIRMADO)")
    void deveAgendarComSucessoQuandoEstabelecimentoSolicita() {
        AgendamentoSolicitarDto dto = new AgendamentoSolicitarDto(null, 1L, 3L, dataHoraValida, FormaPagamento.LOCAL);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(estabelecimentoRepository.findById(2L)).thenReturn(Optional.of(estabelecimento));
        when(expedienteRepository.findByEstabelecimentoId(2L)).thenReturn(List.of(expediente));
        when(agendamentoRepository.existsByEstabelecimentoIdAndDataHoraAndStatusAgendamentoIn(any(), any(), any())).thenReturn(false);
        when(servicoRepository.findById(3L)).thenReturn(Optional.of(servico));

        Agendamento agendamentoSalvo = new Agendamento(dto.dataHora(), dto.formaPagamento(), StatusAgendamento.CONFIRMADO, StatusPagamento.AGUARDANDO_PAGAMENTO, cliente, estabelecimento, servico);
        ReflectionTestUtils.setField(agendamentoSalvo, "id", 11L);
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoSalvo);

        AgendamentoRespostaDto resposta = agendamentoService.solicitarAgendamento(estabelecimento, dto);

        assertNotNull(resposta);
        assertEquals(StatusAgendamento.CONFIRMADO, resposta.statusAgendamento()); // Nasce confirmado!
    }

    @Test
    @DisplayName("Regra de Negócio: Deve falhar se o horário for fora do expediente")
    void deveLancarErroQuandoForaDoExpediente() {
        LocalDateTime foraDeHora = LocalDateTime.of(2026, 5, 20, 22, 0);
        AgendamentoSolicitarDto dto = new AgendamentoSolicitarDto(2L, null, 3L, foraDeHora, FormaPagamento.LOCAL);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(estabelecimentoRepository.findById(2L)).thenReturn(Optional.of(estabelecimento));
        when(expedienteRepository.findByEstabelecimentoId(2L)).thenReturn(List.of(expediente));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class,
                () -> agendamentoService.solicitarAgendamento(cliente, dto));

        assertEquals("O horário selecionado está fora do expediente de funcionamento.", exception.getMessage());
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Regra de Negócio: Trava de Overbooking (Dois clientes no mesmo horário)")
    void deveLancarErroQuandoHorarioJaEstiverOcupado() {
        AgendamentoSolicitarDto dto = new AgendamentoSolicitarDto(2L, null, 3L, dataHoraValida, FormaPagamento.LOCAL);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(estabelecimentoRepository.findById(2L)).thenReturn(Optional.of(estabelecimento));
        when(expedienteRepository.findByEstabelecimentoId(2L)).thenReturn(List.of(expediente));

        when(agendamentoRepository.existsByEstabelecimentoIdAndDataHoraAndStatusAgendamentoIn(any(), any(), any())).thenReturn(true);

        RegraNegocioException exception = assertThrows(RegraNegocioException.class,
                () -> agendamentoService.solicitarAgendamento(cliente, dto));

        assertEquals("Este horário já está reservado. Por favor, escolha outro.", exception.getMessage());
    }

    @Test
    @DisplayName("Segurança: Impedir cancelamento por usuário que não é o dono e não é o estabelecimento")
    void deveLancarErroSeHackerTentarCancelar() {
        Agendamento agendamento = new Agendamento(dataHoraValida, FormaPagamento.LOCAL, StatusAgendamento.CONFIRMADO, StatusPagamento.AGUARDANDO_PAGAMENTO, cliente, estabelecimento, servico);
        ReflectionTestUtils.setField(agendamento, "id", 10L);

        when(agendamentoRepository.findById(10L)).thenReturn(Optional.of(agendamento));

        Long idUsuarioHacker = 99L;

        RegraNegocioException exception = assertThrows(RegraNegocioException.class,
                () -> agendamentoService.cancelarAgendamento(10L, idUsuarioHacker));

        assertEquals("Você não tem permissão para cancelar este agendamento.", exception.getMessage());
    }
}