package estopa.agenda_facil.model.dto;

import estopa.agenda_facil.model.enums.StatusAgendamento;
import estopa.agenda_facil.model.enums.StatusPagamento;

import java.time.LocalDateTime;

public record AgendamentoRespostaDto(
        Long idAgendamento,
        LocalDateTime dataHora,
        String nomeEstabelecimento,
        String especialidade,
        StatusAgendamento statusAgendamento,
        StatusPagamento statusPagamento
) {
}