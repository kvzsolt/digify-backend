package hu.progmasters.blog.controller.category;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static hu.progmasters.blog.controller.constants.Endpoints.CATEGORIES_MAPPING;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class CategoryCreationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test_categoryCreationSuccessful() throws Exception {
        String inputCommand = "{\n" +
                "    \"categoryName\": \"Test Category 1\" \n" +
                "}";

        mockMvc.perform(post(CATEGORIES_MAPPING)
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated());
    }

    @Test
    void test_categoryCreationNameIsEmpty() throws Exception {
        String inputCommand = "{\n" +
                "    \"categoryName\": \"\" \n" +
                "}";

        mockMvc.perform(post(CATEGORIES_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("categoryName")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Category name must not be empty")));
    }

    @Test
    void test_categoryCreationNameIsNull() throws Exception {
        String inputCommand = "{\n" +
                "    \"categoryName\": null \n" +
                "}";

        mockMvc.perform(post(CATEGORIES_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("categoryName")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Category name must not be empty")));
    }

//TODO: A duplikálásnál nem a megfelelő exception fut le, ezt javítani kell

//    @Test
//    void test_categoryCreationNameDuplication() throws Exception {
//        String inputCommand = "{\n" +
//                "    \"categoryName\": \"Test Category 1\" \n" +
//                "}";
//
//        String inputCommand2 = "{\n" +
//                "    \"categoryName\": \"Test Category 1\" \n" +
//                "}";
//
//
//        mockMvc.perform(post(CATEGORIES_MAPPING)
//                        .with(user("testUser").password("testPassword").roles("ADMIN"))
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(inputCommand))
//                .andExpect(status().isCreated());
//
//        mockMvc.perform(post(CATEGORIES_MAPPING)
//                        .with(user("testUser").password("testPassword").roles("ADMIN"))
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(inputCommand2))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.fieldErrors[0].field", is("categoryName")))
//                .andExpect(jsonPath("$.fieldErrors[0].message", is("KITALÁLNI")));
//    }


}
