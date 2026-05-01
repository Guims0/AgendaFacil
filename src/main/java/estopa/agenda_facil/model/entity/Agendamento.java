package estopa.agenda_facil.model.entity;

import estopa.agenda_facil.model.enums.FormaPagamento;
import estopa.agenda_facil.model.enums.StatusAgendamento;
import estopa.agenda_facil.model.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_agendamentos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    private StatusAgendamento statusAgendamento;

    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "estabelecimento_id")
    private Estabelecimento estabelecimento;

    public Agendamento(LocalDateTime dataHora, FormaPagamento formaPagamento, StatusAgendamento statusAgendamento, StatusPagamento statusPagamento, Cliente cliente, Estabelecimento estabelecimento) {
        this.dataHora = dataHora;
        this.formaPagamento = formaPagamento;
        this.statusAgendamento = statusAgendamento;
        this.statusPagamento = statusPagamento;
        this.cliente = cliente;
        this.estabelecimento = estabelecimento;
    }
}
