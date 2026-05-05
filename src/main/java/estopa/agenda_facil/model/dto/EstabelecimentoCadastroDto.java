package estopa.agenda_facil.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CNPJ;

public record EstabelecimentoCadastroDto(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotBlank(message = "O CNPJ é obrigatório")
        @CNPJ(message = "CNPJ inválido")
        String cnpj,

        @NotNull(message = "O intervalo de atendimento não pode ser nulo")
        Integer intervaloAtendimentoMinutos,

        @NotBlank(message = "A descrição da especialidade é obrigatória")
        String descricaoEspecialidade,

        @NotNull(message = "Defina se a aprovação é automática")
        Boolean aprovacaoAutomatica
) {
}