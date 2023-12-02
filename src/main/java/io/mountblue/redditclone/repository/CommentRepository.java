package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
