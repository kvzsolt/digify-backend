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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class TagsListAllTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Test
    void test_emptyList() throws Exception {
        mockMvc.perform(get(TAG_MAPPING)
                        .with(user("testUser").password("testPassword").roles("GUEST")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void test_tagsListAll() throws Exception {
        createDb();
        mockMvc.perform(get(TAG_MAPPING)
                        .with(user("testUser").password("testPassword").roles("GUEST"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tagsName", is("New tag")))
                .andExpect(jsonPath("$[1].tagsName", is("New tag2")))
                .andExpect(jsonPath("$[2].tagsName", is("New tag3")))
                .andExpect(jsonPath("$[3].tagsName", is("New tag4")));
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag2'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag3'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag4'); "
        ).executeUpdate();
    }
}
