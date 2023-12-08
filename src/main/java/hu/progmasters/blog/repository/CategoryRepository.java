package hu.progmasters.blog.repository;

import hu.progmasters.blog.domain.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<PostCategory, Long> {

    @Query("SELECT p FROM PostCategory p WHERE p.categoryName = :category")
    Optional<PostCategory> findByCategoryName(@Param("category") String category);

}
