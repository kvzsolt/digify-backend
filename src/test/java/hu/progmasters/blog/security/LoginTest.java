package hu.progmasters.blog.security;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;
    @Test
    void testLoginSuccess() throws Exception {
        addUser();
        String inputCommand = "{\n" +
                "    \"username\": \"tesztElek\",\n" +
                "    \"password\": \"testPassword\"" +
                "}";

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputCommand))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testLoginInvalidParameters() throws Exception {
        addUser();
        String inputCommand = "{\n" +
                "    \"username\": \"wrongUser\",\n" +
                "    \"password\": \"wrongPassword\"\n" +
                "}";

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Authentication Failed")));
    }

    @Test
    void testLoginMissingPassword() throws Exception {
        addUser();
        String inputCommand = "{\n" +
                "    \"username\": \"testUser\",\n" +
                "    \"password\": \"\"\n" +
                "}";

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Authentication Failed")));
    }
    @Test
    void testLoginMissingUsername() throws Exception {
        addUser();
        String inputCommand = "{\n" +
                "    \"username\": \"\",\n" +
                "    \"password\": \"testPassword\"\n" +
                "}";

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Authentication Failed")));
    }
    private void addUser() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('tesztElek', '$2a$10$RVP8Q2ybES8dJVGbJP54xehMmrzetBxHJZzNNhvBvRmv2gl7bkNt2', 2,'elek@elek.com', true, true);"
        ).executeUpdate();
    }
}
