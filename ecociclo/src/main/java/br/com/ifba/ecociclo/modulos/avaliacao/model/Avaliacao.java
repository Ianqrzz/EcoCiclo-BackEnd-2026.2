package br.com.ifba.ecociclo.modulos.avaliacao.model;

import br.com.ifba.ecociclo.infraestructure.model.PersistenceEntity;
import br.com.ifba.ecociclo.modulos.agendamento.model.Agendamento;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_avaliacoes")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao extends PersistenceEntity {

    @Column(nullable = false)
    private int nota;
    
    @Column(length = 500)
    private String comentario;
    
    @Column(nullable = false)
    private LocalDateTime data;

    @OneToOne(optional = false)
    @JoinColumn(name = "agendamento_id")
    private Agendamento agendamento;

}
