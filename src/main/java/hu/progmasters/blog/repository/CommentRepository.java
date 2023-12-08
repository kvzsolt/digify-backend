package hu.progmasters.blog.repository;

import hu.progmasters.blog.domain.Comment;
import hu.progmasters.blog.dto.comment.ListCommentsRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long id);

}
