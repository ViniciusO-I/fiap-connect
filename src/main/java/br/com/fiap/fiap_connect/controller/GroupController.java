package br.com.fiap.fiap_connect.controller;

import br.com.fiap.fiap_connect.dto.GroupDto;
import br.com.fiap.fiap_connect.dto.SkillDto;
import br.com.fiap.fiap_connect.exception.group.*;
import br.com.fiap.fiap_connect.exception.user.UserNotFoundException;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @GetMapping
    public String list(Model model) {
        model.addAttribute("groups", groupService.list());
        return "groups/index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("group", groupService.findById(id));
        return "groups/detail";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("group", new GroupDto(null, "", 2, null, null, List.of(), new HashSet<>(), 0));
        return prepareFormModel(model, false);
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("group") @Valid GroupDto groupDto,
                         BindingResult result,
                         @RequestParam(value = "skillIds", required = false) List<Integer> skillIds,
                         OAuth2AuthenticationToken authentication,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return prepareFormModel(model, false);
        }

        try {
            Integer ownerId = resolveUserId(authentication);
            GroupDto dtoComSkills = mapDtoWithSkills(groupDto, skillIds, ownerId);
            groupService.create(dtoComSkills, ownerId);
            redirectAttributes.addFlashAttribute("successMessage", "Grupo criado com sucesso!");
            return "redirect:/groups";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return prepareFormModel(model, false);
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("group", groupService.findById(id));
        return prepareFormModel(model, true);
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute("group") @Valid GroupDto groupDto,
                         BindingResult result,
                         @RequestParam(value = "skillIds", required = false) List<Integer> skillIds,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return prepareFormModel(model, true);
        }

        try {
            GroupDto dtoComSkills = mapDtoWithSkills(groupDto, skillIds, groupDto.ownerId());
            groupService.update(id, dtoComSkills);
            redirectAttributes.addFlashAttribute("successMessage", "Grupo atualizado!");
            return "redirect:/groups";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return prepareFormModel(model, true);
        }
    }

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

    @PostMapping("/{groupId}/join")
    public String join(@PathVariable Integer groupId,
                       OAuth2AuthenticationToken authentication,
                       RedirectAttributes redirectAttributes) {
        try {
            Integer userId = resolveUserId(authentication);
            groupService.join(groupId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Você entrou no grupo com sucesso!");
        } catch (UserAlreadyGroupMemberException |
                 GroupNoAvailableSeatsException   |
                 UserLacksRequiredSkillsException  |
                 UserNotFoundException              e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/groups/" + groupId;
    }

    // ── Métodos auxiliares ──────────────────────────────────────────────────────

    /** Prepara o Model para a view de formulário — evita repetição em 4 handlers. */
    private String prepareFormModel(Model model, boolean editMode) {
        model.addAttribute("allSkills", skillService.list());
        model.addAttribute("editMode", editMode);
        return "groups/form";
    }

    /** Mapeia skillIds do formulário para o DTO — evita duplicação em create/update. */
    private GroupDto mapDtoWithSkills(GroupDto origin, List<Integer> skillIds, Integer ownerId) {
        Set<SkillDto> selectedSkills = skillIds != null
                ? skillIds.stream().map(sid -> new SkillDto(sid, "")).collect(Collectors.toSet())
                : new HashSet<>();
        return new GroupDto(
                origin.id(), origin.description(), origin.maxMembers(),
                ownerId, null, List.of(), selectedSkills, 0
        );
    }
}