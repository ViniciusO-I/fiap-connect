package br.com.fiap.fiap_connect.service;

import br.com.fiap.fiap_connect.dto.SkillDto;
import br.com.fiap.fiap_connect.exception.skill.SkillAlreadyRegisteredException;
import br.com.fiap.fiap_connect.exception.skill.SkillDescriptionAlreadyInUseException;
import br.com.fiap.fiap_connect.exception.skill.SkillNotFoundException;
import br.com.fiap.fiap_connect.repository.SkillRepository;
import br.com.fiap.fiap_connect.repository.entities.SkillEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<SkillDto> list() {
        return skillRepository.findAll().stream()
                .map(e -> new SkillDto(e.getId(), e.getDescription()))
                .toList();
    }

    public SkillDto findById(Integer id) {
        SkillEntity entity = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException("Skill não encontrada"));
        return new SkillDto(entity.getId(), entity.getDescription());
    }

    public SkillDto create(SkillDto dto) {
        skillRepository.findByDescriptionIgnoreCase(dto.description())
                .ifPresent(s -> { throw new SkillAlreadyRegisteredException("Skill já cadastrada"); });

        SkillEntity entity = new SkillEntity();
        entity.setDescription(dto.description().trim());
        SkillEntity saved = skillRepository.save(entity);
        return new SkillDto(saved.getId(), saved.getDescription());
    }

    public SkillDto update(Integer id, SkillDto dto) {
        SkillEntity entity = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException("Skill não encontrada"));

        skillRepository.findByDescriptionIgnoreCase(dto.description())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new SkillDescriptionAlreadyInUseException("Descrição já em uso"); });

        entity.setDescription(dto.description().trim());
        SkillEntity saved = skillRepository.save(entity);
        return new SkillDto(saved.getId(), saved.getDescription());
    }

    public void delete(Integer id) {
        SkillEntity entity = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException("Skill não encontrada"));
        skillRepository.delete(entity);
    }
}
