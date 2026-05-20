package estopa.agenda_facil.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ServicoCadastroDto(
        @NotBlank(message = "O nome do serviço é obrigatório.")
        String nome,

        @NotBlank(message = "A descrição do serviço é obrigatória.")
        String descricao,

        @NotNull(message = "O preço é obrigatório.")
        @Positive(message = "O preço do serviço deve ser maior que zero.")
        Double preco
) {
}