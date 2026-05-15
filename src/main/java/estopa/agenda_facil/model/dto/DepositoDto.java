package estopa.agenda_facil.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DepositoDto(
        @NotNull(message = "O valor do depósito é obrigatório.")
        @Positive(message = "O valor do depósito deve ser maior que zero.")
        Double valor
) {
}