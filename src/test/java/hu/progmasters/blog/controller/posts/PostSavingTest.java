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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class PostSavingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;


    @Test
    void test_saveSuccessful() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"title\": \"New title\",\n" +
                "    \"postBody\": \"Example post\",\n" +
                "    \"imgUrl\": \"Example url\", \n" +
                "    \"category\": \"Test Category 1\", \n" +
                "     \"accountId\": 1 \n" +
                "}";

        mockMvc.perform(post(POSTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated());
    }

    @Test
    void test_postBodyIsEmpty() throws Exception {
        String inputCommand = "{\n" +
                "    \"title\": \"New title\",\n" +
                "    \"postBody\": \"\",\n" +
                "    \"imgUrl\": \"Example url\", \n" +
                "    \"category\": \"Test Category 1\", \n" +
                "     \"accountId\": 1 \n" +
                "}";

        mockMvc.perform(post(POSTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("postBody")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Post body must not be empty")));
    }

    @Test
    void test_postBodyIsNull() throws Exception {
        String inputCommand = "{\n" +
                "    \"title\": \"New title\",\n" +
                "    \"imgUrl\": \"Example url\" \n" +
                "}";

        mockMvc.perform(post(POSTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("postBody")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Post body must not be empty")));
    }

    @Test
    void test_titleIsEmpty() throws Exception {
        String inputCommand = "{\n" +
                "    \"title\": \"\",\n" +
                "    \"postBody\": \"Example post\",\n" +
                "    \"imgUrl\": \"Example url\" \n" +
                "}";

        mockMvc.perform(post(POSTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("title")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Title must not be empty")));
    }

    @Test
    void test_titleIsNull() throws Exception {
        String inputCommand = "{\n" +
                "    \"postBody\": \"Example post\",\n" +
                "    \"imgUrl\": \"Example url\" \n" +
                "}";

        mockMvc.perform(post(POSTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("title")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Title must not be empty")));
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('Test Kata','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, account_id, likes) VALUES ('New Post', 'Content','new URL',1,false,false,1, 15); "
        ).executeUpdate();
    }
}
