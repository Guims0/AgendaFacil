package estopa.agenda_facil.model.entity;

import estopa.agenda_facil.model.enums.RoleUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_estabelecimento")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Estabelecimento extends Usuario {

    @Column(unique = true)
    private String cnpj;

    private Integer intervaloAtendimentoMinutos;

    private String descricaoEspecialidade;

    private boolean aprovacaoAutomatica;

    public Estabelecimento(String nome, String email, String senha, String cnpj, Integer intervaloAtendimentoMinutos, String descricaoEspecialidade, boolean aprovacaoAutomatica) {
        super(nome, email, senha, RoleUsuario.ESTABELECIMENTO);
        this.cnpj = cnpj;
        this.intervaloAtendimentoMinutos = intervaloAtendimentoMinutos;
        this.descricaoEspecialidade = descricaoEspecialidade;
        this.aprovacaoAutomatica = aprovacaoAutomatica;
    }
}
