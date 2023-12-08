package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {
    List<Post> findBySubRedditId(Integer subRedditId);

    @Query("""
            SELECT p FROM Post p
            JOIN p.subReddit sr
            JOIN sr.subscribers sub
            WHERE sub.username = :username
            """)
    List<Post> findAllBySubscribedSubReddits(@Param("username") String username);

    @Query("SELECT p FROM Post p ORDER BY p.voteCount DESC")
    List<Post> findAllOrderByVoteCountDesc();

    @Query("SELECT p FROM Post p ORDER BY SIZE(p.commentList) DESC")
    List<Post> findAllPostsOrderedByCommentsSizeDesc();

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllOrderByCreatedAtDesc();

    @Query("SELECT p FROM Post p ORDER BY p.createdAt")
    List<Post> findAllOrderByCreatedAt();

    @Query("SELECT p FROM Post p LEFT JOIN p.tagList t WHERE "+
            "t.name LIKE CONCAT('%', :query, '%') OR "+
            "p.title LIKE CONCAT('%', :query, '%') OR "+
            "p.description LIKE CONCAT('%', :query, '%') "
    )
    List<Post> getPostsBySearch(@Param("query") String query);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user = :user AND p.subReddit = :subReddit AND p.createdAt >= :startDate")
    Integer countPostsByUserInSubredditLast24Hours(
            @Param("user") User user,
            @Param("subReddit") SubReddit subReddit,
            @Param("startDate") LocalDateTime startDate
    );
}
