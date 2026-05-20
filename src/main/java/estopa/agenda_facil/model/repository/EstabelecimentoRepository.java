package estopa.agenda_facil.model.repository;

import estopa.agenda_facil.model.entity.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {

    Optional<Estabelecimento> findByCnpj(String cnpj);
}