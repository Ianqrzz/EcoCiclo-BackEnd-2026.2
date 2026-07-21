package br.com.ifba.ecociclo.modulos.recompensa.model;

import br.com.ifba.ecociclo.infraestructure.model.PersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_recompensas")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Recompensa extends PersistenceEntity {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private int quantidade;

    private String imagem;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private double custoPontos;

    @Column(nullable = false)
    private boolean disponivel;

    @Column(nullable = false)
    private int bloqueado;

}
