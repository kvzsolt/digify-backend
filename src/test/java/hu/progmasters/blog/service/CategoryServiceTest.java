package hu.progmasters.blog.service;

import hu.progmasters.blog.domain.PostCategory;
import hu.progmasters.blog.dto.category.CategoryCreateReq;
import hu.progmasters.blog.dto.category.CategoryListItemReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private EntityManager entityManager;

    @Test
    void test_addCategory() {
        CategoryCreateReq command = new CategoryCreateReq("new category");
        categoryService.addCategory(command);
        PostCategory postCategory = entityManager.find(PostCategory.class, 1L);

        assertEquals(postCategory.getCategoryName(), "new category");
    }

    @Test
    void test_getCategoryListItems() {
        TestPost();
        List<CategoryListItemReq> result = categoryService.getCategoryListItems();
        PostCategory postCategory = entityManager.find(PostCategory.class, 1L);
        PostCategory postCategory2 = entityManager.find(PostCategory.class, 2L);
        PostCategory postCategory3 = entityManager.find(PostCategory.class, 3L);

        assertTrue(result.get(0).getCategory().equals(postCategory.getCategoryName()));
        assertTrue(result.get(1).getCategory().equals(postCategory2.getCategoryName()));
        assertTrue(result.get(2).getCategory().equals(postCategory3.getCategoryName()));
    }

    @Test
    void test_getCategoryByName() {
        TestPost();
        PostCategory postCategory = categoryService.getCategoryByName("Test Category 1");
        assertEquals("Test Category 1", postCategory.getCategoryName());
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
