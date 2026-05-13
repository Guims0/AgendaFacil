package estopa.agenda_facil.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AutenticacaoDto(
        @NotBlank @Email String email,
        @NotBlank String senha
) {
}