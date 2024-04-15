package hu.progmasters.blog.controller.tags;

import hu.progmasters.blog.domain.PostTag;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class TagsEditingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private static final String EDIT_TAG_ID_1_ENDPOINT = TAG_MAPPING + "/1";
    private static final String EDIT_TAG_NOT_EXIST = TAG_MAPPING + "/3";



    @Test
    void test_tagsNotExists() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"tagsName\": \"Edited Tag\" \n" +
                "}";
        mockMvc.perform(put(EDIT_TAG_NOT_EXIST)
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("POST_TAG_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("no tag found with id")));
    }

    @Test
    void test_tagEditSuccessful() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"tagsName\": \"Edited Tag\" \n" +
                "}";
        mockMvc.perform(put(EDIT_TAG_ID_1_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isOk());
        PostTag updatedTag = entityManager.find(PostTag.class, 1L);
        assertEquals("Edited Tag", updatedTag.getTagsName());
    }

    @Test
    void test_tagsNameIsEmpty() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"tagsName\": \"\" \n" +
                "}";
        mockMvc.perform(put(EDIT_TAG_ID_1_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("tagsName")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Tag cannot be empty")));
    }

    @Test
    void test_tagsNameIsNull() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "}";
        mockMvc.perform(put(EDIT_TAG_ID_1_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("tagsName")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Tag cannot be empty")));
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag'); "
        ).executeUpdate();
    }
}
