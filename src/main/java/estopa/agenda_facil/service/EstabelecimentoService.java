package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.dto.EstabelecimentoAtualizacaoDto;
import estopa.agenda_facil.model.dto.EstabelecimentoCadastroDto;
import estopa.agenda_facil.model.entity.Carteira;
import estopa.agenda_facil.model.entity.Estabelecimento;
import estopa.agenda_facil.model.enums.StatusAgendamento;
import estopa.agenda_facil.model.repository.AgendamentoRepository;
import estopa.agenda_facil.model.repository.CarteiraRepository;
import estopa.agenda_facil.model.repository.EstabelecimentoRepository;
import estopa.agenda_facil.model.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final CarteiraRepository carteiraRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encriptadorDeSenha;

    @Transactional
    public void cadastrarEstabelecimento(EstabelecimentoCadastroDto dto) {
        if (estabelecimentoRepository.findByCnpj(dto.cnpj()).isPresent()) {
            throw new RegraNegocioException("Já existe um estabelecimento com este CNPJ.");
        }
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RegraNegocioException("Este e-mail já está em uso por outro usuário.");
        }

        String senhaCriptografada = encriptadorDeSenha.encode(dto.senha());
        Estabelecimento estabelecimento = new Estabelecimento(dto.nome(), dto.email(), senhaCriptografada, dto.cnpj(),
                dto.intervaloAtendimentoMinutos(), dto.descricaoEspecialidade(), dto.aprovacaoAutomatica());
        estabelecimentoRepository.save(estabelecimento);
        carteiraRepository.save(new Carteira(0.0, estabelecimento));
    }

    @Transactional
    public void atualizarEstabelecimento(Long idEstabelecimentoLogado, EstabelecimentoAtualizacaoDto dto) {
        Estabelecimento estab = estabelecimentoRepository.findById(idEstabelecimentoLogado)
                .orElseThrow(() -> new RegraNegocioException("Estabelecimento não encontrado."));

        if (dto.email() != null && !dto.email().equals(estab.getEmail())) {
            if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
                throw new RegraNegocioException("Este e-mail já está em uso por outro usuário.");
            }
        }

        String senhaCriptografada = null;
        if (dto.senha() != null && !dto.senha().isBlank()) {
            senhaCriptografada = encriptadorDeSenha.encode(dto.senha());
        }
        estab.atualizar(dto.nome(), dto.email(), senhaCriptografada, dto.descricaoEspecialidade(), dto.intervaloAtendimentoMinutos(), dto.aprovacaoAutomatica());
    }

    @Transactional
    public void inativarConta(Long idEstabelecimentoLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(idEstabelecimentoLogado)
                .orElseThrow(() -> new RegraNegocioException("Estabelecimento não encontrado."));

        var statusAtivos = List.of(StatusAgendamento.CONFIRMADO, StatusAgendamento.AGUARDANDO_CONFIRMACAO);
        boolean possuiAgendamentosAtivos = agendamentoRepository.findByEstabelecimentoId(idEstabelecimentoLogado).stream()
                .anyMatch(a -> statusAtivos.contains(a.getStatusAgendamento()));

        if (possuiAgendamentosAtivos) {
            throw new RegraNegocioException("Não é possível inativar a conta com agendamentos ativos pendentes.");
        }
        estabelecimento.inativar();
    }
}