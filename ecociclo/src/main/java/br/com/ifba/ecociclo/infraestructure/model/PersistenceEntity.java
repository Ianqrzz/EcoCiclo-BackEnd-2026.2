package br.com.ifba.ecociclo.infraestructure.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PersistenceEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersistenceEntity outra)) return false;
        return Hibernate.getClass(this) == Hibernate.getClass(outra)
                && getId() != null
                && getId().equals(outra.getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}