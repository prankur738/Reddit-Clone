package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.Comment;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    public List<Comment> findByPostId(Integer postId);

    public Optional<Comment> findById(Integer postId);
    public void saveComment(Integer postId, Comment comment);
    public void UpdateComment(Integer commenId,String editComment,Integer postId);
    public void deleteComment(Integer commentId);
    List<Comment> findCommentsBySearchQuery(String query);
}
