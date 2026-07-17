package br.com.ifba.ecociclo.modulos.endereco.repository;

import br.com.ifba.ecociclo.infraestructure.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {
}
