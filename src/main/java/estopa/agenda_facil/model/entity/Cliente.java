package estopa.agenda_facil.model.entity;

import estopa.agenda_facil.model.enums.RoleUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_clientes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cliente extends Usuario{

    @Column(unique = true)
    private String cpf;

    private String telefone;

    public Cliente(String nome, String email, String senha, String cpf, String telefone) {
        super(nome, email, senha, RoleUsuario.CLIENTE);
        this.cpf = cpf;
        this.telefone = telefone;
    }
}
