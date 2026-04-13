package br.com.fiap.fiap_connect.security;

import br.com.fiap.fiap_connect.dto.ProfileEnum;
import br.com.fiap.fiap_connect.repository.UserRepository;
import br.com.fiap.fiap_connect.repository.entities.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String login = oAuth2User.getAttribute("login");   // username do GitHub
        String name  = oAuth2User.getAttribute("name") != null
                ? oAuth2User.getAttribute("name") : login;

        // email sintético: login@github (GitHub não expõe email sem scope extra)
        String email = login + "@github";

        // Busca ou cria usuário no banco
        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity novo = new UserEntity();
            novo.setName(name);
            novo.setEmail(email);
            novo.setPassword("");
            novo.setProfile(ProfileEnum.STUDENT); // padrão: STUDENT
            return userRepository.save(novo);
        });

        // Converte o ProfileEnum em ROLE_ para o Spring Security
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getProfile().name()));

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "login");
    }
}
