package br.com.fiap.fiap_connect.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record GroupDto(
        Integer id,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(min = 3, max = 120, message = "Descrição deve ter entre 3 e 120 caracteres")
        String description,

        @NotNull(message = "Número máximo de membros é obrigatório")
        @Min(value = 2, message = "O grupo deve ter no mínimo 2 membros")
        Integer maxMembers,

        Integer ownerId,
        String ownerName,

        List<UserDto> users,
        Set<SkillDto> skills,

        Integer availableSeats
) {}
