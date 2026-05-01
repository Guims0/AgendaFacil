package estopa.agenda_facil.model.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tb_carteiras")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Carteira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double saldo;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    public Carteira(double saldo, Usuario usuario) {
        this.saldo = saldo;
        this.usuario = usuario;
    }
}
