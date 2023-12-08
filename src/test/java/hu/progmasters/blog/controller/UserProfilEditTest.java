package hu.progmasters.blog.controller;

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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class UserProfilEditTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Test
    void test_profilEditSuccessful() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"realName\": \"edited title\",\n" +
                "    \"dateOfBirth\": \"2023-11-02\",\n" +
                "    \"aboutMe\": \"edited about me\", \n" +
                "    \"profileImageUrl\": \"edited url\" \n" +
                "}";

        mockMvc.perform(put("/api/user/profile/update/1")
                        .with(user("TestUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isOk());
    }

    @Test
    void test_profilNotExist() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"realName\": \"edited title\",\n" +
                "    \"dateOfBirth\": \"2023-11-02\",\n" +
                "    \"aboutMe\": \"edited about me\", \n" +
                "    \"profileImageUrl\": \"edited url\" \n" +
                "}";

        mockMvc.perform(put("/api/user/profile/update/2")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("ACCOUNT_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("No account found with email")));
    }

    private void TestPost() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('TestUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false);"
        ).executeUpdate();
    }
}
