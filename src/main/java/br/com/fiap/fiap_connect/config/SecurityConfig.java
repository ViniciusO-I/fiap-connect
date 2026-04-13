package br.com.fiap.fiap_connect.config;

import br.com.fiap.fiap_connect.security.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos e H2 são públicos
                .requestMatchers("/css/**", "/js/**", "/h2-console/**").permitAll()

                // Apenas ADMINISTRATOR pode criar/editar/deletar skills
                .requestMatchers(HttpMethod.POST, "/skills/new", "/skills/edit/**", "/skills/delete/**")
                    .hasRole("ADMINISTRATOR")
                .requestMatchers("/skills/new", "/skills/edit/**")
                    .hasRole("ADMINISTRATOR")

                // Qualquer usuário autenticado acessa o restante
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo ->
                    userInfo.userService(customOAuth2UserService))
                .defaultSuccessUrl("/", true)
            )
            .exceptionHandling(ex -> ex.accessDeniedPage("/403"))
            .csrf(csrf -> csrf
                // Libera H2 console do CSRF
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                // Necessário para o H2 console (usa iframes)
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}
