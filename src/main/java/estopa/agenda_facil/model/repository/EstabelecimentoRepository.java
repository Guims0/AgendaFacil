package estopa.agenda_facil.model.repository;

import estopa.agenda_facil.model.entity.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {

    Optional<Estabelecimento> findByCnpj(String cnpj);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Estabelecimento e SET e.intervaloAtendimentoMinutos = :intervalo, e.aprovacaoAutomatica = :aprovacao WHERE e.id = :id")
    void atualizarConfiguracaoAgenda(@Param("id") Long id, @Param("intervalo") Integer intervalo, @Param("aprovacao") boolean aprovacaoAutomatica);
}
