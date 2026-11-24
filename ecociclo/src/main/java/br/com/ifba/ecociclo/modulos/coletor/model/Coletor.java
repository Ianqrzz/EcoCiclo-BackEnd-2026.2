package br.com.ifba.ecociclo.modulos.coletor.model;

import br.com.ifba.ecociclo.modulos.associacao.model.Associacao;
import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_coletores")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Coletor extends Usuario {

    @ManyToOne
    @JoinColumn(name = "associacao_id")
    private Associacao associacao;

}
