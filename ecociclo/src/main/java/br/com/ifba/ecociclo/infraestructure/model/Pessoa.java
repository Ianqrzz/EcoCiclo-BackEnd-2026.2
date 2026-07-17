package br.com.ifba.ecociclo.infraestructure.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class Pessoa extends PersistenceEntity {

    @Column(nullable = false)
    private String nome;

    private String telefone;

    @Column(unique = true, nullable = false)
    private String cpf;

}