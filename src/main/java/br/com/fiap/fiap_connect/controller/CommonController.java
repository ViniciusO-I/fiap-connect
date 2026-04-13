package br.com.fiap.fiap_connect.controller;

import br.com.fiap.fiap_connect.repository.UserRepository;
import br.com.fiap.fiap_connect.repository.entities.UserEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

public abstract class CommonController {

    protected final UserRepository userRepository;

    protected CommonController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executado antes de qualquer handler neste controller.
     * Injeta username, avatar e o ID do usuário logado no modelo Thymeleaf.
     */
    @ModelAttribute
    public void preProcessor(Model model, OAuth2AuthenticationToken authentication) {
        if (authentication == null) return;

        String login = authentication.getPrincipal().getAttribute("login");
        String avatar = authentication.getPrincipal().getAttribute("avatar_url");

        model.addAttribute("username", login);
        model.addAttribute("urlAvatar", avatar);

        // Resolve o UserEntity para disponibilizar o ID nas views
        String email = login + "@github";
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        userOpt.ifPresent(u -> {
            model.addAttribute("currentUserId", u.getId());
            model.addAttribute("currentUserProfile", u.getProfile().name());
        });
    }
}
