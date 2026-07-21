package br.com.ifba.ecociclo.modulos.resgate.repository;

import br.com.ifba.ecociclo.modulos.resgate.enums.StatusResgate;
import br.com.ifba.ecociclo.modulos.resgate.model.Resgate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResgateRepository extends JpaRepository<Resgate, UUID> {

    boolean existsByRecompensaId(UUID recompensaId);

    List<Resgate> findByDoador_Id(UUID doadorId);

    List<Resgate> findByStatus(StatusResgate status);

    List<Resgate> findByDoador_IdAndStatus(UUID doadorId, StatusResgate status);
}
