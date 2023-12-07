package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.Comment;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    public List<Comment> findByPostId(Integer postId);
    public Optional<Comment> findById(Integer commentId);
    public void saveComment(Integer postId, Comment comment);
    public void deleteComment(Comment comment);
}
