package hu.progmasters.blog.security;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static hu.progmasters.blog.controller.constants.Endpoints.ACCOUNT_MAPPING;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class RegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String REG_ENDPOINT = ACCOUNT_MAPPING + "/registration";
    @Test
    void test_registrationSuccesful() throws Exception {
        String inputCommand = "{\n" +
                "    \"username\": \"TestKata\",\n" +
                "    \"password\": \"ValidPassword123\",\n" +
                "    \"passwordConfirm\": \"ValidPassword123\",\n" +
                "    \"email\": \"test@example.com\"\n" +
                "}";

        mockMvc.perform(post(REG_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated());
    }

    @Test
    void test_registrationUsernameEmpty() throws Exception {
        String inputCommand = "{\n" +
                "    \"username\": \"\",\n" +
                "    \"password\": \"ValidPassword123\",\n" +
                "    \"password\": \"ValidPassword123\",\n" +
                "    \"email\": \"test@example.com\"\n" +
                "}";

        mockMvc.perform(post(REG_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("username")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItems("Username cannot be empty")));
    }

    @Test
    void test_registrationUsernameTooShort() throws Exception {
        String inputCommand = "{\n" +
                "    \"username\": \"ab\",\n" +
                "    \"password\": \"ValidPassword123\",\n" +
                "    \"email\": \"test@example.com\"\n" +
                "}";

        mockMvc.perform(post(REG_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("username")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItems("Username must be at least 3 characters long")));
    }

    @Test
    void test_registrationUsernameContainsWhitespace() throws Exception {
        String inputCommand = "{\n" +
                "    \"username\": \"abc def\",\n" +
                "    \"password\": \"ValidPassword123\",\n" +
                "    \"email\": \"test@example.com\"\n" +
                "}";

        mockMvc.perform(post(REG_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field",hasItem("username")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItems("Username cannot contain whitespaces")));
    }
    @Test
    void test_registrationPasswordEmpty() throws Exception {
        String inputCommand = "{\n" +
                "    \"username\": \"ValidUsername\",\n" +
                "    \"password\": \"\",\n" +
                "    \"email\": \"test@example.com\"\n" +
                "}";

        mockMvc.perform(post(REG_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("password")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItems("Password must be at least 8 characters long")));
    }

    @Test
    void test_registrationPasswordTooShort() throws Exception {
        String inputCommand = "{\n" +
                "    \"username\": \"ValidUsername\",\n" +
                "    \"password\": \"short\",\n" +
                "    \"email\": \"test@example.com\"\n" +
                "}";

        mockMvc.perform(post(REG_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("password")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItems("Password must be at least 8 characters long")));
    }

    @Test
    void test_registrationPasswordContainsWhitespace() throws Exception {
        String inputCommand = "{\n" +
                "    \"username\": \"ValidUsername\",\n" +
                "    \"password\": \"Invalid Pass\",\n" +
                "    \"email\": \"test@example.com\"\n" +
                "}";

        mockMvc.perform(post(REG_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("password")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItem("Password cannot contain whitespaces")));
    }

    @Test
    void test_registrationEmailEmpty() throws Exception {
        String inputCommand = "{\n" +
                "    \"username\": \"ValidUsername\",\n" +
                "    \"password\": \"ValidPassword123\",\n" +
                "    \"email\": \"\"\n" +
                "}";

        mockMvc.perform(post(REG_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("email")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItem("Email cannot be empty")));
    }

    @Test
    void test_registrationInvalidEmailFormat() throws Exception {
        String inputCommand = "{\n" +
                "    \"username\": \"ValidUsername\",\n" +
                "    \"password\": \"ValidPassword123\",\n" +
                "    \"email\": \"invalidEmail\"\n" +
                "}";

        mockMvc.perform(post(REG_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("email")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItem("Invalid email format")));
    }

}

