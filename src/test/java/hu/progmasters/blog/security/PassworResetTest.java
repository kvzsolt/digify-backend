package hu.progmasters.blog.security;

import hu.progmasters.blog.domain.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class PassworResetTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Test
    void test_requestPasswordResetSuccessful() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"email\": \"blogprogmasters@gmail.com\" \n" +
                "}";
        mockMvc.perform(post("/api/user/request")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isOk());
    }

    @Test
    void test_requestPasswordResetWrongEmail() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"email\": \"blogp@gmail.com\" \n" +
                "}";
        mockMvc.perform(post("/api/user/request")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("ACCOUNT_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("No account found with email")));
    }
    @Test
    void test_requestPasswordResetInvalidEmail() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"email\": \"blogpgmail.com\" \n" +
                "}";

        mockMvc.perform(post("/api/user/request")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("email")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Invalid email format")));
    }
    @Test
    void test_requestPasswordResetEmailIsEmpty() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"email\": \"\" \n" +
                "}";

        mockMvc.perform(post("/api/user/request")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("email")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Email cannot be empty")));
    }

    @Test
    void test_resetPasswordSuccessful() throws Exception {
        TestPost();
        Account account = entityManager.find(Account.class,1L);
        String token = jwtTokenUtil.generatePasswordResetToken(account);
        String inputCommand = "{\n" +
                "    \"password\": \"Boglarkaneni\",\n" +
                "    \"passwordConfirm\": \"Boglarkaneni\"\n" +
                "}";


        mockMvc.perform(post("/api/user/reset")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .param("token",token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputCommand))
                .andExpect(status().isOk());
    }
    @Test
    void test_resetPasswordShortPassword() throws Exception {
        TestPost();
        Account account = entityManager.find(Account.class,1L);
        String token = jwtTokenUtil.generatePasswordResetToken(account);
        String inputCommand = "{\n" +
                "    \"password\": \"Bogla\",\n" +
                "    \"passwordConfirm\": \"Bogla\"\n" +
                "}";

        mockMvc.perform(post("/api/user/reset")
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
        TestPost();
        Account account = entityManager.find(Account.class,1L);
        String token = jwtTokenUtil.generatePasswordResetToken(account);
        String inputCommand = "{\n" +
                "    \"password\": \"\" \n" +
                "}";

        mockMvc.perform(post("/api/user/reset")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .param("token", token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("password")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItems("Password cannot be empty")));
    }



    private void TestPost() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('testUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blogprogmasters@gmail.com',false,false); "
        ).executeUpdate();
    }
}
