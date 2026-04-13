package br.com.fiap.fiap_connect.service;

import br.com.fiap.fiap_connect.dto.GroupDto;
import br.com.fiap.fiap_connect.dto.SkillDto;
import br.com.fiap.fiap_connect.dto.UserDto;
import br.com.fiap.fiap_connect.exception.group.*;
import br.com.fiap.fiap_connect.exception.skill.SkillNotFoundException;
import br.com.fiap.fiap_connect.exception.user.UserNotFoundException;
import br.com.fiap.fiap_connect.repository.GroupRepository;
import br.com.fiap.fiap_connect.repository.SkillRepository;
import br.com.fiap.fiap_connect.repository.UserRepository;
import br.com.fiap.fiap_connect.repository.entities.GroupEntity;
import br.com.fiap.fiap_connect.repository.entities.SkillEntity;
import br.com.fiap.fiap_connect.repository.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public GroupService(GroupRepository groupRepository,
                        UserRepository userRepository,
                        SkillRepository skillRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    public List<GroupDto> list() {
        return groupRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public GroupDto findById(Integer id) {
        return toDto(groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Grupo não encontrado")));
    }

    public GroupDto create(GroupDto dto, Integer ownerId) {
        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new GroupOwnerNotFoundException("Dono do grupo não encontrado"));

        List<SkillEntity> requiredSkills = skillRepository.findAllById(dto.skills().stream()
                .map(SkillDto::id).toList());
        if (requiredSkills.size() != dto.skills().size()) {
            throw new SkillNotFoundException("Uma ou mais skills obrigatórias não encontradas");
        }

        GroupEntity group = new GroupEntity();
        group.setDescription(dto.description().trim());
        group.setMaxMembers(dto.maxMembers());
        group.setOwner(owner);
        group.setSkills(new HashSet<>(requiredSkills));
        group.getUsers().add(owner); // dono entra automaticamente

        return toDto(groupRepository.save(group));
    }

    public GroupDto update(Integer id, GroupDto dto) {
        GroupEntity group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Grupo não encontrado"));

        List<SkillEntity> requiredSkills = skillRepository.findAllById(dto.skills().stream()
                .map(SkillDto::id).toList());
        if (requiredSkills.size() != dto.skills().size()) {
            throw new SkillNotFoundException("Uma ou mais skills não encontradas");
        }

        group.setDescription(dto.description().trim());
        group.setMaxMembers(dto.maxMembers());
        group.setSkills(new HashSet<>(requiredSkills));

        return toDto(groupRepository.save(group));
    }

    public void delete(Integer id) {
        GroupEntity group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Grupo não encontrado"));
        groupRepository.delete(group);
    }

    /** Fluxo principal: usuário entra em um grupo */
    public GroupDto join(Integer groupId, Integer userId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Grupo não encontrado"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        // Regra 1: usuário já é membro?
        if (group.getUsers().stream().anyMatch(m -> m.getId().equals(userId))) {
            throw new UserAlreadyGroupMemberException("Você já é membro deste grupo");
        }

        // Regra 2: há vagas disponíveis?
        if (group.getUsers().size() >= group.getMaxMembers()) {
            throw new GroupNoAvailableSeatsException("Grupo sem vagas disponíveis");
        }

        // Regra 3: usuário possui todas as skills obrigatórias?
        if (!userHasAllRequiredSkills(user, group.getSkills())) {
            throw new UserLacksRequiredSkillsException("Você não possui todas as skills exigidas pelo grupo");
        }

        group.getUsers().add(user);
        return toDto(groupRepository.save(group));
    }

    private boolean userHasAllRequiredSkills(UserEntity user, Set<SkillEntity> required) {
        Set<Integer> userSkillIds = user.getSkills().stream()
                .map(SkillEntity::getId)
                .collect(Collectors.toSet());
        return required.stream().allMatch(s -> userSkillIds.contains(s.getId()));
    }

    private GroupDto toDto(GroupEntity e) {
        Set<SkillDto> skills = e.getSkills().stream()
                .map(s -> new SkillDto(s.getId(), s.getDescription()))
                .collect(Collectors.toSet());

        List<UserDto> users = e.getUsers().stream()
                .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail(), null, u.getProfile(), List.of()))
                .toList();

        int available = e.getMaxMembers() - e.getUsers().size();

        return new GroupDto(
                e.getId(),
                e.getDescription(),
                e.getMaxMembers(),
                e.getOwner() != null ? e.getOwner().getId() : null,
                e.getOwner() != null ? e.getOwner().getName() : null,
                users,
                skills,
                available
        );
    }
}
