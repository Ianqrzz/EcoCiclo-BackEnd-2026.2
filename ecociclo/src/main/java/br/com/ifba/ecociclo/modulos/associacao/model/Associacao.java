package br.com.ifba.ecociclo.modulos.associacao.model;

import br.com.ifba.ecociclo.modulos.endereco.model.Endereco;
import br.com.ifba.ecociclo.infraestructure.model.PersistenceEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_associacoes")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Associacao extends PersistenceEntity {

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String cnpj;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

}
