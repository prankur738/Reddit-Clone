package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {
    public List<Post> findBySubRedditId(Integer subRedditId);
}
