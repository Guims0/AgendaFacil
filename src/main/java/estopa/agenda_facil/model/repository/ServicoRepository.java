package estopa.agenda_facil.model.repository;

import estopa.agenda_facil.model.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByEstabelecimentoId(Long estabelecimentoId);
}