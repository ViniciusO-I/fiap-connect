package br.com.fiap.fiap_connect.service;

import br.com.fiap.fiap_connect.dto.ProfileEnum;
import br.com.fiap.fiap_connect.dto.SkillDto;
import br.com.fiap.fiap_connect.dto.UserDto;
import br.com.fiap.fiap_connect.exception.skill.SkillNotFoundException;
import br.com.fiap.fiap_connect.exception.user.EmailAlreadyRegisteredException;
import br.com.fiap.fiap_connect.exception.user.UserNotFoundException;
import br.com.fiap.fiap_connect.repository.SkillRepository;
import br.com.fiap.fiap_connect.repository.UserRepository;
import br.com.fiap.fiap_connect.repository.entities.SkillEntity;
import br.com.fiap.fiap_connect.repository.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public UserService(UserRepository userRepository, SkillRepository skillRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    public List<UserDto> list() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public UserDto findById(Integer id) {
        return toDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado")));
    }

    public UserDto findByEmail(String email) {
        return toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado")));
    }

    /** Cria ou atualiza usuário via OAuth2 (login pelo GitHub) */
    public UserEntity findOrCreateByEmail(String email, String name) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity novo = new UserEntity();
            novo.setName(name);
            novo.setEmail(email);
            novo.setPassword("");
            novo.setProfile(ProfileEnum.STUDENT);
            return userRepository.save(novo);
        });
    }

    public UserDto create(UserDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyRegisteredException("E-mail já cadastrado");
        }
        UserEntity entity = new UserEntity();
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPassword("");
        entity.setProfile(dto.profile() != null ? dto.profile() : ProfileEnum.STUDENT);
        return toDto(userRepository.save(entity));
    }

    public UserDto addSkills(Integer userId, List<Integer> skillIds) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        List<SkillEntity> skills = skillRepository.findAllById(skillIds);
        if (skills.size() != skillIds.size()) {
            throw new SkillNotFoundException("Uma ou mais skills não encontradas");
        }

        Set<SkillEntity> merged = new HashSet<>(user.getSkills());
        merged.addAll(skills);
        user.setSkills(merged);
        return toDto(userRepository.save(user));
    }

    private UserDto toDto(UserEntity e) {
        List<SkillDto> skills = e.getSkills().stream()
                .map(s -> new SkillDto(s.getId(), s.getDescription()))
                .toList();
        return new UserDto(e.getId(), e.getName(), e.getEmail(), null, e.getProfile(), skills);
    }
}
