package br.com.fiap.fiap_connect.controller;

import br.com.fiap.fiap_connect.dto.GroupDto;
import br.com.fiap.fiap_connect.dto.SkillDto;
import br.com.fiap.fiap_connect.exception.group.*;
import br.com.fiap.fiap_connect.repository.UserRepository;
import br.com.fiap.fiap_connect.service.GroupService;
import br.com.fiap.fiap_connect.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/groups")
public class GroupController extends CommonController {

    private final GroupService groupService;
    private final SkillService skillService;

    public GroupController(GroupService groupService,
                           SkillService skillService,
                           UserRepository userRepository) {
        super(userRepository);
        this.groupService = groupService;
        this.skillService = skillService;
    }

    // ── Listar grupos ───────────────────────────────────────────────────────────
    @GetMapping
    public String list(Model model) {
        model.addAttribute("groups", groupService.list());
        return "groups/index";
    }

    // ── Detalhe de um grupo ─────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("group", groupService.findById(id));
        return "groups/detail";
    }

    // ── Formulário de novo grupo ────────────────────────────────────────────────
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("group", new GroupDto(null, "", 2, null, null, List.of(), java.util.Set.of(), 0));
        model.addAttribute("allSkills", skillService.list());
        model.addAttribute("editMode", false);
        return "groups/form";
    }

    // ── Criar grupo ─────────────────────────────────────────────────────────────
    @PostMapping("/new")
    public String create(@ModelAttribute("group") @Valid GroupDto groupDto,
                         BindingResult result,
                         @RequestParam(value = "skillIds", required = false) List<Integer> skillIds,
                         OAuth2AuthenticationToken authentication,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allSkills", skillService.list());
            model.addAttribute("editMode", false);
            return "groups/form";
        }

        // Resolve ID do usuário logado
        String login = authentication.getPrincipal().getAttribute("login");
        String email = login + "@github";
        Integer ownerId = userRepository.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow();

        // Monta lista de SkillDto com os IDs selecionados
        List<SkillDto> selectedSkills = skillIds != null
                ? skillIds.stream().map(sid -> new SkillDto(sid, "")).toList()
                : List.of();

        GroupDto dtoComSkills = new GroupDto(
                groupDto.id(), groupDto.description(), groupDto.maxMembers(),
                ownerId, null, List.of(), new java.util.HashSet<>(selectedSkills), 0
        );

        try {
            groupService.create(dtoComSkills, ownerId);
            redirectAttributes.addFlashAttribute("successMessage", "Grupo criado com sucesso!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("allSkills", skillService.list());
            model.addAttribute("editMode", false);
            return "groups/form";
        }
        return "redirect:/groups";
    }

    // ── Formulário de edição ────────────────────────────────────────────────────
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("group", groupService.findById(id));
        model.addAttribute("allSkills", skillService.list());
        model.addAttribute("editMode", true);
        return "groups/form";
    }

    // ── Salvar edição ───────────────────────────────────────────────────────────
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute("group") @Valid GroupDto groupDto,
                         BindingResult result,
                         @RequestParam(value = "skillIds", required = false) List<Integer> skillIds,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allSkills", skillService.list());
            model.addAttribute("editMode", true);
            return "groups/form";
        }

        List<SkillDto> selectedSkills = skillIds != null
                ? skillIds.stream().map(sid -> new SkillDto(sid, "")).toList()
                : List.of();

        GroupDto dtoComSkills = new GroupDto(
                groupDto.id(), groupDto.description(), groupDto.maxMembers(),
                groupDto.ownerId(), null, List.of(), new java.util.HashSet<>(selectedSkills), 0
        );

        try {
            groupService.update(id, dtoComSkills);
            redirectAttributes.addFlashAttribute("successMessage", "Grupo atualizado!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("allSkills", skillService.list());
            model.addAttribute("editMode", true);
            return "groups/form";
        }
        return "redirect:/groups";
    }

    // ── Deletar grupo ───────────────────────────────────────────────────────────
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            groupService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Grupo removido!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/groups";
    }

    // ── FLUXO PRINCIPAL: entrar no grupo ────────────────────────────────────────
    @PostMapping("/{groupId}/join")
    public String join(@PathVariable Integer groupId,
                       OAuth2AuthenticationToken authentication,
                       RedirectAttributes redirectAttributes) {
        String login = authentication.getPrincipal().getAttribute("login");
        String email = login + "@github";

        Integer userId = userRepository.findByEmail(email)
                .map(u -> u.getId())
                .orElse(null);

        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/groups/" + groupId;
        }

        try {
            groupService.join(groupId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Você entrou no grupo com sucesso!");
        } catch (UserAlreadyGroupMemberException |
                 GroupNoAvailableSeatsException |
                 UserLacksRequiredSkillsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/groups/" + groupId;
    }
}
