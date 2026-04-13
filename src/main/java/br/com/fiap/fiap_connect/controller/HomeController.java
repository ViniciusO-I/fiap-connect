package br.com.fiap.fiap_connect.controller;

import br.com.fiap.fiap_connect.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController extends CommonController {

    public HomeController(UserRepository userRepository) {
        super(userRepository);
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/403")
    public String forbidden() {
        return "error/403";
    }
}
