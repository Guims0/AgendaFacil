package estopa.agenda_facil.model.dto;

import estopa.agenda_facil.model.enums.FormaPagamento;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AgendamentoSolicitarDto(
        Long estabelecimentoId,

        Long clienteId,

        @NotNull(message = "O ID do serviço é obrigatório")
        Long servicoId,

        @NotNull(message = "A data e hora são obrigatórias")
        @Future(message = "A data do agendamento deve estar no futuro")
        LocalDateTime dataHora,

        @NotNull(message = "A forma de pagamento é obrigatória")
        FormaPagamento formaPagamento
) {
}