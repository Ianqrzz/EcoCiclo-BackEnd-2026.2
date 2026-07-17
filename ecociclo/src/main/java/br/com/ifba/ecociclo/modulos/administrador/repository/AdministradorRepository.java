package br.com.ifba.ecociclo.modulos.administrador.repository;

import br.com.ifba.ecociclo.modulos.administrador.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, UUID> {
}
