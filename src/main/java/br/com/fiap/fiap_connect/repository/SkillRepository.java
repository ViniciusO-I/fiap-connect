package br.com.fiap.fiap_connect.repository;

import br.com.fiap.fiap_connect.repository.entities.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, Integer> {
    Optional<SkillEntity> findByDescriptionIgnoreCase(String description);
}
