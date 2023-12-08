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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class CategoryListAllTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Test
    void test_CategoryListAll() throws Exception {
        TestPost();
        mockMvc.perform(get("/api/categories")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category", is("Test Category 1")))
                .andExpect(jsonPath("$[1].category", is("Test Category 2")))
                .andExpect(jsonPath("$[2].category", is("Test Category 3")))
                .andExpect(jsonPath("$[3].category", is("Test Category 4")));
    }

    @Test
    void test_categoryEmptyList() throws Exception {
        mockMvc.perform(get("/api/categories")
                        .with(user("testUser").password("testPassword").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    private void TestPost() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 2'); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 3'); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 4');"
        ).executeUpdate();
    }

}

