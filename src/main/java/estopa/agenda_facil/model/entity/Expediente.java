package estopa.agenda_facil.model.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "tb_expedientes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Expediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek diaDaSemana;

    private LocalTime horaInicio;

    private LocalTime horaFim;

    @ManyToOne
    @JoinColumn(name = "estabelecimento_id")
    private Estabelecimento estabelecimento;

    public Expediente(DayOfWeek diaDaSemana, LocalTime horaInicio, LocalTime horaFim, Estabelecimento estabelecimento) {
        this.diaDaSemana = diaDaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.estabelecimento = estabelecimento;
    }
}
