package gruppe_b.quizduell.authserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
// @Import({ SecurityConfig.class, TokenService.class })
class HomeControllerTest {

    static {
        System.setProperty("DB_PORT", "3306");
        System.setProperty("DB_HOSTNAME", "localhost");
        System.setProperty("DB_USERNAME", "root");
        System.setProperty("DB_PASSWORD", "root");
    }

    @Autowired
    MockMvc mvc;

    @Test
    void whenUnauthenticatedThen401() throws Exception {
        this.mvc.perform(get("/v1/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void withMockUserStatusIsOK() throws Exception {
        this.mvc.perform(get("/v1/")).andExpect(status().isOk());
    }
}