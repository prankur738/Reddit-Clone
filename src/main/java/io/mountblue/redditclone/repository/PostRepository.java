package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
