package br.com.fiap.fiap_connect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SkillDto(
        Integer id,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(min = 2, max = 80, message = "Descrição deve ter entre 2 e 80 caracteres")
        String description
) {}
