package br.com.ifba.ecociclo.modulos.doador.repository;

import br.com.ifba.ecociclo.modulos.doador.model.Doador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoadorRepository extends JpaRepository<Doador, UUID> {
}
