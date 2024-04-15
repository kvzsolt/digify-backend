package hu.progmasters.blog.repository;

import hu.progmasters.blog.domain.PostTag;
import hu.progmasters.blog.dto.tag.ListPostTagsRes;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TagsRepositoryTest {

    @Autowired
    private TagsRepository tagsRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testFindAllOrderBy() {
        PostTag tag = new PostTag();
        PostTag tag2 = new PostTag();
        PostTag tag3 = new PostTag();
        ListPostTagsRes listPostTagsRes = modelMapper.map(tag, ListPostTagsRes.class);
        ListPostTagsRes listPostTagsRes2 = modelMapper.map(tag2, ListPostTagsRes.class);
        ListPostTagsRes listPostTagsRes3 = modelMapper.map(tag3, ListPostTagsRes.class);

        entityManager.persist(tag);
        entityManager.persist(tag2);
        entityManager.persist(tag3);

        entityManager.flush();

        List<ListPostTagsRes> result = tagsRepository.findAllOrderBy();

        assertTrue(result.contains(listPostTagsRes));
        assertTrue(result.contains(listPostTagsRes2));
        assertTrue(result.contains(listPostTagsRes3));
    }

    @Test
    public void testFindByTagByNameByPostId() {
        TestPost();

        Optional<PostTag> result = tagsRepository.findByTagByNameByPostId("New tag", 1L);
        PostTag tags = entityManager.find(PostTag.class, 1L);
        assertEquals(result.get().getTagsName(), tags.getTagsName());
    }

    private void TestPost() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, likes) VALUES ('New Post', 'Content','new URL',1,false,false,0); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag2'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag3'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag4'); " +
                        "INSERT INTO post_post_tag ( post_tag_id, post_id ) VALUES (1,1); "
        ).executeUpdate();
    }


}
