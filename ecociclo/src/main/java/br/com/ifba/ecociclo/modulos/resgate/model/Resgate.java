package br.com.ifba.ecociclo.modulos.resgate.model;

import br.com.ifba.ecociclo.infraestructure.model.PersistenceEntity;
import br.com.ifba.ecociclo.modulos.doador.model.Doador;
import br.com.ifba.ecociclo.modulos.recompensa.model.Recompensa;
import br.com.ifba.ecociclo.modulos.resgate.enums.StatusResgate;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_resgates")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Resgate extends PersistenceEntity {

    private LocalDateTime data;
    
    private double pontosGastos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusResgate status;

    @ManyToOne
    @JoinColumn(name = "doador_id")
    private Doador doador;

    @ManyToOne
    @JoinColumn(name = "recompensa_id")
    private Recompensa recompensa;

}
