package br.com.fiap.fiap_connect.repository;

import br.com.fiap.fiap_connect.repository.entities.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Integer> {
}
