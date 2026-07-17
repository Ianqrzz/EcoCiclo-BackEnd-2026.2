package br.com.ifba.ecociclo.modulos.avaliacao.model;

import br.com.ifba.ecociclo.infraestructure.model.PersistenceEntity;
import br.com.ifba.ecociclo.modulos.agendamento.model.Agendamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_avaliacoes")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Avaliacao extends PersistenceEntity {

    private int nota;
    
    @Column(length = 500)
    private String comentario;
    
    private LocalDateTime data;

    @OneToOne
    @JoinColumn(name = "agendamento_id")
    private Agendamento agendamento;

}
