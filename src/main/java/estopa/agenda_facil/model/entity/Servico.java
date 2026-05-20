package estopa.agenda_facil.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_servicos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String descricao;

    private Double preco;

    @ManyToOne
    @JoinColumn(name = "estabelecimento_id")
    private Estabelecimento estabelecimento;

    public Servico(String nome, String descricao, Double preco, Estabelecimento estabelecimento) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estabelecimento = estabelecimento;
    }

    public void atualizar(String nome, String descricao, Double preco) {
        if (nome != null && !nome.isBlank()) this.nome = nome;
        if (descricao != null && !descricao.isBlank()) this.descricao = descricao;
        if (preco != null) this.preco = preco;
    }
}