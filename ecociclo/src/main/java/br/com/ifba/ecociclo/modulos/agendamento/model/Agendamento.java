package br.com.ifba.ecociclo.modulos.agendamento.model;

import br.com.ifba.ecociclo.infraestructure.model.PersistenceEntity;
import br.com.ifba.ecociclo.modulos.agendamento.enums.StatusAgendamento;
import br.com.ifba.ecociclo.modulos.coletor.model.Coletor;
import br.com.ifba.ecociclo.modulos.doacao.model.Doacao;
import br.com.ifba.ecociclo.modulos.doador.model.Doador;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_agendamentos")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Agendamento extends PersistenceEntity {

    private LocalDateTime dataColeta;
    private LocalDateTime dataCriacao;
    
    @Column(length = 500)
    private String observacoes;
    
    private double pontosGerados;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;

    @ManyToOne
    @JoinColumn(name = "doador_id")
    private Doador doador;

    @ManyToOne
    @JoinColumn(name = "coletor_id")
    private Coletor coletor;

    @OneToOne
    @JoinColumn(name = "doacao_id")
    private Doacao doacao;

}
