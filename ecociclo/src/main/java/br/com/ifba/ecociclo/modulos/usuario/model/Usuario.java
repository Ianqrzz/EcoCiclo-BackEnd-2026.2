package br.com.ifba.ecociclo.modulos.usuario.model;

import br.com.ifba.ecociclo.infraestructure.model.Pessoa;
import br.com.ifba.ecociclo.modulos.endereco.model.Endereco;
import br.com.ifba.ecociclo.modulos.usuario.enums.TipoPerfil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tb_usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class Usuario extends Pessoa implements UserDetails, java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPerfil perfil;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Endereco> enderecos = new ArrayList<>();

    public void adicionarEndereco(Endereco endereco) {
        if (endereco == null) {
            return;
        }

        if (this.enderecos == null) {
            this.enderecos = new ArrayList<>();
        }

        if (!this.enderecos.contains(endereco)) {
            this.enderecos.add(endereco);
        }

        endereco.setUsuario(this);
    }

    public void removerEndereco(Endereco endereco) {
        if (endereco == null || this.enderecos == null) {
            return;
        }

        this.enderecos.remove(endereco);
        if (endereco.getUsuario() == this) {
            endereco.setUsuario(null);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.perfil.name()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
