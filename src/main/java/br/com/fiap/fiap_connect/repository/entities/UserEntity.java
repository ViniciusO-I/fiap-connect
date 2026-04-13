package br.com.fiap.fiap_connect.repository.entities;

import br.com.fiap.fiap_connect.dto.ProfileEnum;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class UserEntity extends AuditDataEntity implements Serializable {

    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private ProfileEnum profile;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_skill",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<SkillEntity> skills = new HashSet<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public ProfileEnum getProfile() { return profile; }
    public void setProfile(ProfileEnum profile) { this.profile = profile; }
    public Set<SkillEntity> getSkills() { return skills; }
    public void setSkills(Set<SkillEntity> skills) { this.skills = skills; }
}
