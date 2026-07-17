package br.com.ifba.ecociclo.modulos.coletor.model;

import br.com.ifba.ecociclo.modulos.associacao.model.Associacao;
import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_coletores")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Coletor extends Usuario {

    @ManyToOne
    @JoinColumn(name = "associacao_id")
    private Associacao associacao;

}
