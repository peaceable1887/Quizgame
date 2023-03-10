package gruppe_b.quizduell.authserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;

import gruppe_b.quizduell.application.interfaces.RequestHandler;
import gruppe_b.quizduell.application.user.commands.create_user.CreateUserCommand;
import gruppe_b.quizduell.application.user.queries.get_details.GetUserDetailQuery;
import gruppe_b.quizduell.authserver.common.AuthHelper;
import gruppe_b.quizduell.authserver.common.UserJwtDto;
import gruppe_b.quizduell.domain.entities.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    static {
        System.setProperty("DB_PORT", "3306");
        System.setProperty("DB_HOSTNAME", "localhost");
        System.setProperty("DB_USERNAME", "root");
        System.setProperty("DB_PASSWORD", "root");
    }

    @Autowired
    MockMvc mvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RequestHandler<CreateUserCommand, User> createUserHandler;

    @Autowired
    AuthHelper authHelper;

    @Autowired
    RequestHandler<GetUserDetailQuery, User> getUserHandler;

    @Test
    void whenAuthenticatedThenSaysHelloUser() throws Exception {
        // Arrange
        String endcodePswd = passwordEncoder.encode("password");

        CreateUserCommand command = new CreateUserCommand(
                "john",
                "test@test.de",
                endcodePswd,
                "salt");
        createUserHandler.handle(command);

        MvcResult result = this.mvc.perform(get("/v1/token")
                .with(httpBasic("john", "password")))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String jwtJson = result.getResponse().getContentAsString();
        UserJwtDto jwtDto = objectMapper.readValue(jwtJson, UserJwtDto.class);

        // Act & Assert
        this.mvc.perform(get("/v1/")
                .header("Authorization", "Bearer " + jwtDto.token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, ")))
                .andReturn();
    }

    @Test
    void whenRegisterThenRegistered() throws Exception {
        // Arrange
        JSONObject jObject = new JSONObject();
        jObject.put("name", "RegisterTestName");
        jObject.put("password", "password");

        // Act & Assert
        this.mvc.perform(post("/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jObject.toJSONString()))
                .andExpect(status().isCreated());
    }

    @Test
    void whenRegisterWithMailThenRegisteredWithMail() throws Exception {
        // Arrange
        JSONObject jObject = new JSONObject();
        jObject.put("name", "RegisterTestName2");
        jObject.put("password", "password");
        jObject.put("mail", "test@test.com");

        // Act & Assert
        this.mvc.perform(post("/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jObject.toJSONString()))
                .andExpect(status().isCreated());

        // Assert
        User user = getUserHandler.handle(new GetUserDetailQuery("RegisterTestName2"));
        assertNotNull(user);
        assertEquals("RegisterTestName2", user.getName());
        assertEquals("test@test.com", user.getMail());
    }

    @Test
    void whenRegisteredThenUserGetToken() throws Exception {
        // Arrange
        JSONObject jObject = new JSONObject();
        jObject.put("name", "RegisterTestName3");
        jObject.put("password", "password");

        // Act
        this.mvc.perform(post("/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jObject.toJSONString()));

        MvcResult result = this.mvc.perform(get("/v1/token")
                .with(httpBasic("RegisterTestName3", "password")))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String jwtJson = result.getResponse().getContentAsString();
        UserJwtDto jwtDto = objectMapper.readValue(jwtJson, UserJwtDto.class);

        // Assert
        assertNotNull(jwtDto);
        assertNotNull(jwtDto.token);
        assertNotNull(jwtDto.userId);
        assertNotNull(jwtDto.username);
        assertEquals(560, jwtDto.token.length());
    }

    @Test
    void whenUnregisteredThen401() throws Exception {
        // Arrange

        // Act

        // Assert
        this.mvc.perform(get("/v1/token")
                .with(httpBasic("unregisteredTest", "password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenRegisterWithEmptyNameWhenFail() throws Exception {
        // Arrange
        JSONObject jObject = new JSONObject();
        jObject.put("name", "");
        jObject.put("password", "password");

        // Act & Assert
        this.mvc.perform(post("/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jObject.toJSONString()))
                .andExpect(status().isBadRequest());
        // .andExpect(jsonPath("$.name", Is.is("Name is mandatory")));
    }
}