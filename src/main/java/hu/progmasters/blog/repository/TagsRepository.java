package hu.progmasters.blog.repository;

import hu.progmasters.blog.domain.PostTag;
import hu.progmasters.blog.dto.tag.ListPostTagsRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagsRepository extends JpaRepository<PostTag,Long> {
    @Query("SELECT t FROM PostTag t JOIN t.posts p WHERE t.tagsName=:tagsName AND p.id=:postId")
    Optional<PostTag> findByTagByNameByPostId(@Param("tagsName") String tagsName,
                                              @Param("postId") Long id);
    @Query("SELECT NEW hu.progmasters.blog.dto.tag.ListPostTagsRes(t.tagsName, t.posts.size) FROM PostTag t ")
    List<ListPostTagsRes> findAllOrderBy();
}
