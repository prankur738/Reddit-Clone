package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    public List<Comment> findByPostId(Integer postId);
}
