package br.com.ifba.ecociclo.modulos.recompensa.repository;

import br.com.ifba.ecociclo.modulos.recompensa.model.Recompensa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecompensaRepository extends JpaRepository<Recompensa, UUID> {
}
