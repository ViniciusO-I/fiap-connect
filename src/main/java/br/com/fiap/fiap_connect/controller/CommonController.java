package br.com.fiap.fiap_connect.controller;

import br.com.fiap.fiap_connect.exception.user.UserNotFoundException;
import br.com.fiap.fiap_connect.repository.UserRepository;
import br.com.fiap.fiap_connect.repository.entities.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
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
     * Injeta username, avatar, ID do usuário logado e menu ativo no modelo Thymeleaf.
     */
    @ModelAttribute
    public void preProcessor(Model model,
                             OAuth2AuthenticationToken authentication,
                             HttpServletRequest request) {
        // ── Menu ativo para destacar o link correto na navbar ──────────────
        String uri = request.getRequestURI();
        if (uri.startsWith("/skills"))      model.addAttribute("activeMenu", "skills");
        else if (uri.startsWith("/groups")) model.addAttribute("activeMenu", "groups");
        else if (uri.startsWith("/users"))  model.addAttribute("activeMenu", "users");
        else                                model.addAttribute("activeMenu", "");

        if (authentication == null) return;

        String login  = authentication.getPrincipal().getAttribute("login");
        String avatar = authentication.getPrincipal().getAttribute("avatar_url");

        model.addAttribute("username",   login);
        model.addAttribute("urlAvatar",  avatar);

        String email = login + "@github";
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        userOpt.ifPresent(u -> {
            model.addAttribute("currentUserId",      u.getId());
            model.addAttribute("currentUserProfile", u.getProfile().name());
        });
    }

    /**
     * Centraliza a resolução do ID do usuário autenticado via OAuth2.
     * Substitui o bloco login→email→findByEmail→getId() que estava
     * duplicado em múltiplos controllers.
     */
    protected Integer resolveUserId(OAuth2AuthenticationToken auth) {
        String login = auth.getPrincipal().getAttribute("login");
        String email = login + "@github";
        return userRepository.findByEmail(email)
                .map(UserEntity::getId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
    }
}