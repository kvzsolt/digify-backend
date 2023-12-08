package hu.progmasters.blog.repository;

import hu.progmasters.blog.domain.PostCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CategoryRepositoryTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testFindByCategoryName(){
        TestPost();
        Optional<PostCategory> result = Optional.ofNullable(categoryRepository.findByCategoryName("Test Category 1").orElse(null));
        PostCategory category = entityManager.find(PostCategory.class,1L);
        assertTrue(category.getCategoryName().equals(result.get().getCategoryName()));
    }
    private void TestPost() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, likes) VALUES ('New Post', 'Content','new URL',1 , false, false, 0); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, likes) VALUES ('New Post2', 'Content2','new URL2',1 , false, false, 0); " +
                        "INSERT INTO comment (post_Id, comment_Body) VALUES (1,'comment');" +
                        "INSERT INTO comment (post_Id, comment_Body) VALUES (1,'comment2');" +
                        "INSERT INTO comment (post_Id, comment_Body) VALUES (1,'comment3');" +
                        "INSERT INTO comment (post_Id, comment_Body ) VALUES (2, 'comment4' );"
        ).executeUpdate();
    }
}
