package br.com.ifba.ecociclo.modulos.endereco.model;

import br.com.ifba.ecociclo.infraestructure.model.PersistenceEntity;
import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_enderecos")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Endereco extends PersistenceEntity {

    private String logradouro;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Usuario usuario;
}
