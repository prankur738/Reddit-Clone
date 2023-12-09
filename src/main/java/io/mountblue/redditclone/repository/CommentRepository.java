package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    public List<Comment> findByPostId(Integer postId);

    @Query("SELECT c FROM Comment c LEFT JOIN c.post p WHERE "+
            "p.title LIKE CONCAT('%', :query, '%') OR "+
            "p.description LIKE CONCAT('%', :query, '%') OR "+
            "c.text LIKE CONCAT('%', :query, '%') "
    )
    List<Comment> getCommentsBySearch(@Param("query") String query);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.voteCount DESC")
    List<Comment> findCommentsByPostIdOrderByVotesDesc(@Param("postId") Integer postId);

}
