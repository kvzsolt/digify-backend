package hu.progmasters.blog.controller.tags;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static hu.progmasters.blog.controller.constants.Endpoints.TAG_MAPPING;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class TagsSavingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private static final String SAVE_TAG_ID_1 = TAG_MAPPING + "/1";

    @Test
    void test_saveSuccessfulOneTag() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"tagsName\": \" Tag \" \n" +
                "}";

        mockMvc.perform(post(SAVE_TAG_ID_1)
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated());
    }

    @Test
    void test_tagsNameIsEmpty() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"tagsName\": \"\" \n" +
                "}";

        mockMvc.perform(post(SAVE_TAG_ID_1)
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("tagsName")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Tag cannot be empty")));
    }

    @Test
    void test_tagsNameIsNull() throws Exception {
        String inputCommand = "{\n" +
                "    \"tagsName\":\"\" \n" +
                "}";

        mockMvc.perform(post(SAVE_TAG_ID_1)
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("tagsName")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Tag cannot be empty")));
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('AuthorUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, account_id, likes) VALUES ('New Post', 'Content','new URL',1,false,false, 1, 15); "

        ).executeUpdate();
    }
}
