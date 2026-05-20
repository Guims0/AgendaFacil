package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.dto.ClienteCadastroDto;
import estopa.agenda_facil.model.entity.Agendamento;
import estopa.agenda_facil.model.entity.Carteira;
import estopa.agenda_facil.model.entity.Cliente;
import estopa.agenda_facil.model.entity.Usuario;
import estopa.agenda_facil.model.enums.StatusAgendamento;
import estopa.agenda_facil.model.repository.AgendamentoRepository;
import estopa.agenda_facil.model.repository.CarteiraRepository;
import estopa.agenda_facil.model.repository.ClienteRepository;
import estopa.agenda_facil.model.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock private ClienteRepository clienteRepository;
    @Mock private CarteiraRepository carteiraRepository;
    @Mock private AgendamentoRepository agendamentoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private ClienteCadastroDto cadastroDto;
    private Cliente cliente;

    @BeforeEach
    void setup() {
        cadastroDto = new ClienteCadastroDto("Maria", "maria@email.com", "senha123", "99988877766", "21999999999");

        cliente = new Cliente("Maria", "maria@email.com", "hash123", "99988877766", "21999999999");
        ReflectionTestUtils.setField(cliente, "id", 1L);
        ReflectionTestUtils.setField(cliente, "ativo", true);
    }

    @Test
    @DisplayName("Cadastro: Deve salvar cliente com senha criptografada e criar carteira vazia")
    void deveCadastrarClienteComSucesso() {
        when(clienteRepository.findByCpf(cadastroDto.cpf())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(cadastroDto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(cadastroDto.senha())).thenReturn("HASH_SECRETO");

        clienteService.cadastrarCliente(cadastroDto);

        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(carteiraRepository, times(1)).save(any(Carteira.class));
    }

    @Test
    @DisplayName("Cadastro: Deve barrar criação se E-mail já existir na base geral de Usuários")
    void deveLancarErroSeEmailJaExistir() {
        when(clienteRepository.findByCpf(cadastroDto.cpf())).thenReturn(Optional.empty());

        when(usuarioRepository.findByEmail(cadastroDto.email())).thenReturn(Optional.of(mock(Usuario.class)));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class,
                () -> clienteService.cadastrarCliente(cadastroDto));

        assertEquals("Este e-mail já está em uso por outro usuário.", exception.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Exclusão: Soft Delete funciona se não tiver agendamentos ativos")
    void deveInativarContaComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(agendamentoRepository.findByClienteId(1L)).thenReturn(List.of());

        clienteService.inativarConta(1L);

        assertFalse(cliente.isAtivo());
    }

    @Test
    @DisplayName("Exclusão: Soft Delete é bloqueado se o cliente tiver um agendamento pendente")
    void deveLancarErroAoInativarContaComAgendamentosPendentes() {
        Agendamento agendamentoPendente = mock(Agendamento.class);
        when(agendamentoPendente.getStatusAgendamento()).thenReturn(StatusAgendamento.CONFIRMADO);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(agendamentoRepository.findByClienteId(1L)).thenReturn(List.of(agendamentoPendente));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class,
                () -> clienteService.inativarConta(1L));

        assertEquals("Não é possível inativar sua conta com agendamentos ativos. Cancele-os primeiro.", exception.getMessage());
        assertTrue(cliente.isAtivo());
    }
}