package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.dto.ClienteCadastroDto;
import estopa.agenda_facil.model.entity.Carteira;
import estopa.agenda_facil.model.entity.Cliente;
import estopa.agenda_facil.model.repository.CarteiraRepository;
import estopa.agenda_facil.model.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final CarteiraRepository carteiraRepository;
    private final PasswordEncoder encriptadorDeSenha;

    @Transactional
    public void cadastrarCliente(ClienteCadastroDto dto) {

        if (clienteRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new RegraNegocioException("Já existe um cliente cadastrado com este CPF.");
        }

        String senhaCriptografada = encriptadorDeSenha.encode(dto.senha());

        Cliente cliente = new Cliente(
                dto.nome(),
                dto.email(),
                senhaCriptografada,
                dto.cpf(),
                dto.telefone()
        );

        clienteRepository.save(cliente);

        Carteira carteira = new Carteira(0.0, cliente);
        carteiraRepository.save(carteira);
    }
}