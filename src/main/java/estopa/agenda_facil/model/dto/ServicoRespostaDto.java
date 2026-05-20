package estopa.agenda_facil.model.dto;

public record ServicoRespostaDto(
        Long id,
        String nome,
        String descricao,
        Double preco
) {
}