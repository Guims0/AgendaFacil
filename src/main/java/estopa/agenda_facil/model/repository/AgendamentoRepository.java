package estopa.agenda_facil.model.repository;

import estopa.agenda_facil.model.entity.Agendamento;
import estopa.agenda_facil.model.enums.StatusAgendamento;
import estopa.agenda_facil.model.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByClienteId(Long clienteId);

    List<Agendamento> findByEstabelecimentoId(Long estabelecimentoId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Agendamento a SET a.statusPagamento = :novoStatus WHERE a.id = :id")
    void atualizarStatusPagamento(@Param("id") Long id, @Param("novoStatus") StatusPagamento novoStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Agendamento a SET a.statusAgendamento = :novoStatus WHERE a.id = :id")
    void atualizarStatusAgendamento(@Param("id") Long id, @Param("novoStatus") StatusAgendamento novoStatus);
}