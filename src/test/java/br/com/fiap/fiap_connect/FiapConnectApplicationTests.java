package br.com.fiap.fiap_connect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.github.client-id=test-id",
    "spring.security.oauth2.client.registration.github.client-secret=test-secret"
})
class FiapConnectApplicationTests {

    @Test
    void contextLoads() {
    }
}
