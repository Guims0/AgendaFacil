package estopa.agenda_facil.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ClienteAtualizacaoDto(

        String nome,

        @Email(message = "Formato de e-mail inválido")
        String email,

        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha,

        String telefone
) {
}