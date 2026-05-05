package estopa.agenda_facil.model.repository;

import estopa.agenda_facil.model.entity.Carteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarteiraRepository extends JpaRepository<Carteira, Long> {

    Optional<Carteira> findByUsuarioId(Long usuarioId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Carteira c SET c.saldo = c.saldo + :valor WHERE c.id = :id")
    void adicionarSaldo(@Param("id") Long id, @Param("valor") double valor);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Carteira c SET c.saldo = c.saldo - :valor WHERE c.id = :id")
    void debitarSaldo(@Param("id") Long id, @Param("valor") double valor);
}
