package hu.progmasters.blog.controller.account;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class UserProfilInfoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private static final String USER_PROFILE_INFO_ENDPOINT = ACCOUNT_MAPPING + "/profile/1";
    private static final String USER_PROFILE_NOT_EXIST_INFO_ENDPOINT = ACCOUNT_MAPPING + "/profile/2";

    @Test
    void test_profilListSuccesful() throws Exception {
        createDb();
        mockMvc.perform(get(USER_PROFILE_INFO_ENDPOINT)
                        .with(user("TestUser").password("testPassword").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("TestUser")))
                .andExpect(jsonPath("$.email", is("blog@blog.com")));
    }

    @Test
    void test_profilNotExist() throws Exception {
        createDb();
        mockMvc.perform(get(USER_PROFILE_NOT_EXIST_INFO_ENDPOINT)
                        .with(user("TestUser").password("testPassword").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("ACCOUNT_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("No account found with email")));
    }


    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('TestUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); "
        ).executeUpdate();
    }
}
