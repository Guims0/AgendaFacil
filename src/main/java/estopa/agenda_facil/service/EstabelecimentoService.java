package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.dto.EstabelecimentoCadastroDto;
import estopa.agenda_facil.model.entity.Carteira;
import estopa.agenda_facil.model.entity.Estabelecimento;
import estopa.agenda_facil.model.repository.CarteiraRepository;
import estopa.agenda_facil.model.repository.EstabelecimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final CarteiraRepository carteiraRepository;

    @Transactional
    public void cadastrarEstabelecimento(EstabelecimentoCadastroDto dto) {

        if (estabelecimentoRepository.findByCnpj(dto.cnpj()).isPresent()) {
            throw new RegraNegocioException("Já existe um estabelecimento com este CNPJ.");
        }

        Estabelecimento estabelecimento = new Estabelecimento(
                dto.nome(),
                dto.email(),
                dto.senha(),
                dto.cnpj(),
                dto.intervaloAtendimentoMinutos(),
                dto.descricaoEspecialidade(),
                dto.aprovacaoAutomatica()
        );

        estabelecimentoRepository.save(estabelecimento);

        Carteira carteira = new Carteira(0.0, estabelecimento);
        carteiraRepository.save(carteira);
    }
}