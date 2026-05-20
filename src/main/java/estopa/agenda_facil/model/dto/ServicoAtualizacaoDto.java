package estopa.agenda_facil.model.dto;

import jakarta.validation.constraints.Positive;

public record ServicoAtualizacaoDto(

        String nome,

        String descricao,

        @Positive(message = "O preço do serviço deve ser maior que zero.")
        Double preco
) {
}