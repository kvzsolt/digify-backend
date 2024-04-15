package hu.progmasters.blog.controller.posts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static hu.progmasters.blog.controller.constants.Endpoints.POSTS_MAPPING;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class PostToPdfByIdTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private static final String CONVERT_POST_NOT_EXIST_ENDPOINT = POSTS_MAPPING + "/pdf/2";
    private static final String CONVERT_POST_ID_1_ENDPOINT= POSTS_MAPPING + "/pdf/1";


    @Test
    void test_postNotExists() throws Exception {
        createDb();
        mockMvc.perform(get(CONVERT_POST_NOT_EXIST_ENDPOINT)
                        .with(user("PremiumUser").password("testPassword").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("POST_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("no post found with id")));
    }

    @Test
    void test_postToPdfSuccessful() throws Exception {
        createDb();
        mockMvc.perform(get(CONVERT_POST_ID_1_ENDPOINT)
                        .with(user("PremiumUser").password("testPassword").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void test_postToPdfNotAPremiumUser() throws Exception {
        createDb();
        mockMvc.perform(get(CONVERT_POST_ID_1_ENDPOINT)
                        .with(user("NormalUser").password("testPassword").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('NormalUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('PremiumUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,true); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, account_id, likes) VALUES ('New Post', 'Content','new URL',1 , false, false, 1, 15); " +
                        "INSERT INTO comment (post_Id, account_id, comment_Body) VALUES (1, 1,'comment');"
        ).executeUpdate();
    }
}
