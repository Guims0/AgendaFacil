package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.dto.ClienteAtualizacaoDto;
import estopa.agenda_facil.model.dto.ClienteCadastroDto;
import estopa.agenda_facil.model.entity.Carteira;
import estopa.agenda_facil.model.entity.Cliente;
import estopa.agenda_facil.model.enums.StatusAgendamento;
import estopa.agenda_facil.model.repository.AgendamentoRepository;
import estopa.agenda_facil.model.repository.CarteiraRepository;
import estopa.agenda_facil.model.repository.ClienteRepository;
import estopa.agenda_facil.model.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final CarteiraRepository carteiraRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encriptadorDeSenha;

    @Transactional
    public void cadastrarCliente(ClienteCadastroDto dto) {
        if (clienteRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new RegraNegocioException("Já existe um cliente cadastrado com este CPF.");
        }
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RegraNegocioException("Este e-mail já está em uso por outro usuário.");
        }

        String senhaCriptografada = encriptadorDeSenha.encode(dto.senha());
        Cliente cliente = new Cliente(dto.nome(), dto.email(), senhaCriptografada, dto.cpf(), dto.telefone());
        clienteRepository.save(cliente);
        carteiraRepository.save(new Carteira(0.0, cliente));
    }

    @Transactional
    public void atualizarCliente(Long idClienteLogado, ClienteAtualizacaoDto dto) {
        Cliente cliente = clienteRepository.findById(idClienteLogado)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado."));

        if (dto.email() != null && !dto.email().equals(cliente.getEmail())) {
            if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
                throw new RegraNegocioException("Este e-mail já está em uso por outro usuário.");
            }
        }

        String senhaCriptografada = null;
        if (dto.senha() != null && !dto.senha().isBlank()) {
            senhaCriptografada = encriptadorDeSenha.encode(dto.senha());
        }
        cliente.atualizar(dto.nome(), dto.email(), senhaCriptografada, dto.telefone());
    }

    @Transactional
    public void inativarConta(Long idClienteLogado) {
        Cliente cliente = clienteRepository.findById(idClienteLogado)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado."));
        var statusAtivos = List.of(StatusAgendamento.CONFIRMADO, StatusAgendamento.AGUARDANDO_CONFIRMACAO);
        boolean possuiAgendamentosAtivos = agendamentoRepository.findByClienteId(idClienteLogado).stream()
                .anyMatch(a -> statusAtivos.contains(a.getStatusAgendamento()));

        if (possuiAgendamentosAtivos) {
            throw new RegraNegocioException("Não é possível inativar sua conta com agendamentos ativos. Cancele-os primeiro.");
        }
        cliente.inativar();
    }
}