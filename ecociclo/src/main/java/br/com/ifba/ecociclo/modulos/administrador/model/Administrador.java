package br.com.ifba.ecociclo.modulos.administrador.model;

import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_administradores")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Administrador extends Usuario {
    // Herda todos os atributos de Usuario e Pessoa
}
