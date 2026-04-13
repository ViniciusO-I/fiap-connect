package br.com.fiap.fiap_connect.controller;

import br.com.fiap.fiap_connect.repository.UserRepository;
import br.com.fiap.fiap_connect.service.SkillService;
import br.com.fiap.fiap_connect.service.UserService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController extends CommonController {

    private final UserService userService;
    private final SkillService skillService;

    public UserController(UserService userService,
                          SkillService skillService,
                          UserRepository userRepository) {
        super(userRepository);
        this.userService = userService;
        this.skillService = skillService;
    }

    // ── Listar todos os usuários ────────────────────────────────────────────────
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.list());
        return "users/index";
    }

    // ── Perfil do usuário logado ────────────────────────────────────────────────
    @GetMapping("/profile")
    public String profile(OAuth2AuthenticationToken authentication, Model model) {
        String login = authentication.getPrincipal().getAttribute("login");
        String email = login + "@github";
        var user = userRepository.findByEmail(email);
        user.ifPresent(u -> {
            model.addAttribute("profileUser", userService.findById(u.getId()));
            model.addAttribute("allSkills", skillService.list());
        });
        return "users/profile";
    }

    // ── Adicionar skills ao perfil do usuário logado ────────────────────────────
    @PostMapping("/profile/skills")
    public String addSkills(@RequestParam(value = "skillIds", required = false) List<Integer> skillIds,
                            OAuth2AuthenticationToken authentication,
                            RedirectAttributes redirectAttributes) {
        if (skillIds == null || skillIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selecione pelo menos uma skill.");
            return "redirect:/users/profile";
        }

        String login = authentication.getPrincipal().getAttribute("login");
        String email = login + "@github";
        Integer userId = userRepository.findByEmail(email).map(u -> u.getId()).orElse(null);

        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/users/profile";
        }

        try {
            userService.addSkills(userId, skillIds);
            redirectAttributes.addFlashAttribute("successMessage", "Skills adicionadas com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/profile";
    }
}
