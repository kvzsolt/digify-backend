package hu.progmasters.blog.security;

import hu.progmasters.blog.domain.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static hu.progmasters.blog.controller.constants.Endpoints.ACCOUNT_MAPPING;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class PasswordResetTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final String PASSWORD_RESET_REQUEST_ENDPOINT = ACCOUNT_MAPPING + "/request";
    private static final String PASSWORD_RESET_ENDPOINT = ACCOUNT_MAPPING + "/reset";
    @Test
    void test_requestPasswordResetSuccessful() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"email\": \"blogprogmasters@gmail.com\" \n" +
                "}";
        mockMvc.perform(post(PASSWORD_RESET_REQUEST_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isOk());
    }

    @Test
    void test_requestPasswordResetWrongEmail() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"email\": \"blogp@gmail.com\" \n" +
                "}";
        mockMvc.perform(post(PASSWORD_RESET_REQUEST_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("ACCOUNT_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("No account found with email")));
    }
    @Test
    void test_requestPasswordResetInvalidEmail() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"email\": \"blogpgmail.com\" \n" +
                "}";

        mockMvc.perform(post(PASSWORD_RESET_REQUEST_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("email")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Invalid email format")));
    }
    @Test
    void test_requestPasswordResetEmailIsEmpty() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"email\": \"\" \n" +
                "}";

        mockMvc.perform(post(PASSWORD_RESET_REQUEST_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("email")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Email cannot be empty")));
    }

    @Test
    void test_resetPasswordSuccessful() throws Exception {
        createDb();
        Account account = entityManager.find(Account.class,1L);
        String token = jwtTokenUtil.generatePasswordResetToken(account);
        String inputCommand = "{\n" +
                "    \"password\": \"Boglarkaneni\",\n" +
                "    \"passwordConfirm\": \"Boglarkaneni\"\n" +
                "}";


        mockMvc.perform(post(PASSWORD_RESET_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .param("token",token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputCommand))
                .andExpect(status().isOk());
    }
    @Test
    void test_resetPasswordShortPassword() throws Exception {
        createDb();
        Account account = entityManager.find(Account.class,1L);
        String token = jwtTokenUtil.generatePasswordResetToken(account);
        String inputCommand = "{\n" +
                "    \"password\": \"Bogla\",\n" +
                "    \"passwordConfirm\": \"Bogla\"\n" +
                "}";

        mockMvc.perform(post(PASSWORD_RESET_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .param("token",token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("password")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItems("Password must be at least 8 characters long")));
    }
    @Test
    void test_resetPasswordIsEmpty() throws Exception {
        createDb();
        Account account = entityManager.find(Account.class,1L);
        String token = jwtTokenUtil.generatePasswordResetToken(account);
        String inputCommand = "{\n" +
                "    \"password\": \"\" \n" +
                "}";

        mockMvc.perform(post(PASSWORD_RESET_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .param("token", token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("password")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItems("Password cannot be empty")));
    }



    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('testUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blogprogmasters@gmail.com',false,false); "
        ).executeUpdate();
    }
}
