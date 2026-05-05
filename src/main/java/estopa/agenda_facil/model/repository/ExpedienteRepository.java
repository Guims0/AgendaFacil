package estopa.agenda_facil.model.repository;

import estopa.agenda_facil.model.entity.Expediente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpedienteRepository extends JpaRepository<Expediente, Long> {

    List<Expediente> findByEstabelecimentoId(Long estabelecimentoId);
}