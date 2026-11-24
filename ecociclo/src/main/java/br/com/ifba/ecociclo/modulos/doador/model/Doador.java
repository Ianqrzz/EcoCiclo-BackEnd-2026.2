package br.com.ifba.ecociclo.modulos.doador.model;

import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_doadores")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Doador extends Usuario {

    @Column(nullable = false)
    private double pontuacao;

}
