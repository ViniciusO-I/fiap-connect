package br.com.fiap.fiap_connect.repository.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skill_entity")
public class SkillEntity extends AuditDataEntity implements Serializable {

    private String description;

    @ManyToMany(mappedBy = "skills")
    private Set<UserEntity> users = new HashSet<>();

    @ManyToMany(mappedBy = "skills")
    private Set<GroupEntity> groups = new HashSet<>();

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Set<UserEntity> getUsers() { return users; }
    public void setUsers(Set<UserEntity> users) { this.users = users; }
    public Set<GroupEntity> getGroups() { return groups; }
    public void setGroups(Set<GroupEntity> groups) { this.groups = groups; }
}
