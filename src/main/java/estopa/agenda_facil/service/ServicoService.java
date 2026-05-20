package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.dto.ServicoCadastroDto;
import estopa.agenda_facil.model.dto.ServicoAtualizacaoDto;
import estopa.agenda_facil.model.dto.ServicoRespostaDto;
import estopa.agenda_facil.model.entity.Estabelecimento;
import estopa.agenda_facil.model.entity.Servico;
import estopa.agenda_facil.model.repository.EstabelecimentoRepository;
import estopa.agenda_facil.model.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    @Transactional
    public void cadastrarServico(Long idEstabelecimentoLogado, ServicoCadastroDto dto) {

        Estabelecimento estabelecimento = estabelecimentoRepository.findById(idEstabelecimentoLogado)
                .orElseThrow(() -> new RegraNegocioException("Estabelecimento não encontrado."));

        Servico servico = new Servico(
                dto.nome(),
                dto.descricao(),
                dto.preco(),
                estabelecimento
        );

        servicoRepository.save(servico);
    }

    public List<ServicoRespostaDto> listarServicosDoEstabelecimento(Long idEstabelecimento) {

        return servicoRepository.findByEstabelecimentoId(idEstabelecimento)
                .stream()
                .map(s -> new ServicoRespostaDto(s.getId(), s.getNome(), s.getDescricao(), s.getPreco()))
                .toList();
    }

    @Transactional
    public void atualizarServico(Long idEstabelecimentoLogado, Long idServico, ServicoAtualizacaoDto dto) {

        Servico servico = servicoRepository.findById(idServico)
                .orElseThrow(() -> new RegraNegocioException("Serviço não encontrado."));

        if (!servico.getEstabelecimento().getId().equals(idEstabelecimentoLogado)) {
            throw new RegraNegocioException("Você não tem permissão para editar este serviço.");
        }

        servico.atualizar(dto.nome(), dto.descricao(), dto.preco());
    }

    @Transactional
    public void deletarServico(Long idEstabelecimentoLogado, Long idServico) {

        Servico servico = servicoRepository.findById(idServico)
                .orElseThrow(() -> new RegraNegocioException("Serviço não encontrado."));

        if (!servico.getEstabelecimento().getId().equals(idEstabelecimentoLogado)) {
            throw new RegraNegocioException("Você não tem permissão para excluir este serviço.");
        }

        servicoRepository.delete(servico);
    }
}