package br.com.fiap.fiap_connect.controller;

import br.com.fiap.fiap_connect.dto.SkillDto;
import br.com.fiap.fiap_connect.exception.skill.SkillAlreadyRegisteredException;
import br.com.fiap.fiap_connect.exception.skill.SkillDescriptionAlreadyInUseException;
import br.com.fiap.fiap_connect.repository.UserRepository;
import br.com.fiap.fiap_connect.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/skills")
public class SkillController extends CommonController {

    private final SkillService skillService;

    public SkillController(SkillService skillService, UserRepository userRepository) {
        super(userRepository);
        this.skillService = skillService;
    }

    // ── Listar todas as skills ──────────────────────────────────────────────────
    @GetMapping
    public String list(Model model) {
        model.addAttribute("skills", skillService.list());
        return "skills/index";
    }

    // ── Exibir formulário de nova skill (só ADMINISTRATOR) ─────────────────────
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("skill", new SkillDto(null, ""));
        model.addAttribute("editMode", false);
        return "skills/form";
    }

    // ── Salvar nova skill ───────────────────────────────────────────────────────
    @PostMapping("/new")
    public String create(@ModelAttribute("skill") @Valid SkillDto skillDto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("editMode", false);
            return "skills/form";
        }
        try {
            skillService.create(skillDto);
            redirectAttributes.addFlashAttribute("successMessage", "Skill criada com sucesso!");
        } catch (SkillAlreadyRegisteredException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("editMode", false);
            return "skills/form";
        }
        return "redirect:/skills";
    }

    // ── Exibir formulário de edição (só ADMINISTRATOR) ─────────────────────────
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("skill", skillService.findById(id));
        model.addAttribute("editMode", true);
        return "skills/form";
    }

    // ── Salvar edição ───────────────────────────────────────────────────────────
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute("skill") @Valid SkillDto skillDto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("editMode", true);
            return "skills/form";
        }
        try {
            skillService.update(id, skillDto);
            redirectAttributes.addFlashAttribute("successMessage", "Skill atualizada com sucesso!");
        } catch (SkillDescriptionAlreadyInUseException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("editMode", true);
            return "skills/form";
        }
        return "redirect:/skills";
    }

    // ── Deletar skill (só ADMINISTRATOR) ───────────────────────────────────────
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            skillService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Skill removida com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não foi possível remover: " + e.getMessage());
        }
        return "redirect:/skills";
    }
}
