package hu.progmasters.blog.controller.tags;

import hu.progmasters.blog.domain.PostTag;
import hu.progmasters.blog.exception.posts.NotFoundPostTagException;
import hu.progmasters.blog.service.TagService;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class TagsDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TagService tagService;

    private static final String DELETE_TAG_ID_1_ENDPOINT = TAG_MAPPING + "/1";
    private static final String DELETE_TAG_NOT_EXIST = TAG_MAPPING + "/3";
    @Test
    void test_saveSuccessDeleteTag() throws Exception {
        createDb();
        PostTag tag = entityManager.find(PostTag.class, 1L);
        assertNotNull(tag);
        mockMvc.perform(delete(DELETE_TAG_ID_1_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        assertThrows(NotFoundPostTagException.class, () -> tagService.findPostTagById(1L));
    }

    @Test
    void test_tagNotExists() throws Exception {
        createDb();
        mockMvc.perform(delete(DELETE_TAG_NOT_EXIST)
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("POST_TAG_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("no tag found with id")));
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag'); "
        ).executeUpdate();
    }
}
