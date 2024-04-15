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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class SearchPostsByTitleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private static final String SEARCH_POST_BY_TITLE = POSTS_MAPPING + "/search?title=new";
    private static final String SEARCH_POST_BY_EMPTY_TITLE = POSTS_MAPPING + "/search?title=";
    private static final String SEARCH_POST_BY_NOT_EXISTING_TITLE = POSTS_MAPPING + "/search?title=aladin";
    @Test
    void test_searchByTitle() throws Exception {
        createDb();
        mockMvc.perform(get(SEARCH_POST_BY_TITLE)
                        .with(user("testUser").password("testPassword").roles("GUEST"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("New Post4")))
                .andExpect(jsonPath("$[1].title", is("New Post3")))
                .andExpect(jsonPath("$[2].title", is("New Post2")))
                .andExpect(jsonPath("$[3].title", is("New Post")));
    }

    @Test
    void test_searchByTitleEmptyTitle() throws Exception {
        createDb();
        mockMvc.perform(get(SEARCH_POST_BY_EMPTY_TITLE)
                        .with(user("testUser").password("testPassword").roles("GUEST"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("New Post4")))
                .andExpect(jsonPath("$[1].title", is("New Post3")))
                .andExpect(jsonPath("$[2].title", is("New Post2")))
                .andExpect(jsonPath("$[3].title", is("New Post")));
    }

    @Test
    void test_searchByTitleNotExist() throws Exception {
        createDb();
        mockMvc.perform(get(SEARCH_POST_BY_NOT_EXISTING_TITLE)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('AuthorUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " + "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, category_id, scheduled, account_id, likes) VALUES ('New Post', 'Content','new URL',false,1, false, 1, 15); " +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, category_id, scheduled, account_id, likes) VALUES ('New Post2', 'Content2','new URL2',false,1, false,1, 15); " +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, category_id, scheduled, account_id, likes) VALUES ('New Post3', 'Content3','new URL3',false, 1, false,1, 15); " +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, category_id, scheduled, account_id, likes) VALUES ('New Post4', 'Content4','new URL4',false,1, false, 1, 15); "

        ).executeUpdate();
    }
}

