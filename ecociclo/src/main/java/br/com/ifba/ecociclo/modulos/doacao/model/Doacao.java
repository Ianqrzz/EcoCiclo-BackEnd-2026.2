package br.com.ifba.ecociclo.modulos.doacao.model;

import br.com.ifba.ecociclo.infraestructure.model.PersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_doacoes")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Doacao extends PersistenceEntity {

    @Column(nullable = false)
    private String nome;

    private int quantidade;

    private String imagem;

    private float peso;

}
