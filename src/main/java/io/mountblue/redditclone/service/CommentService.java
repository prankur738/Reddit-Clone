package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.Comment;

import java.util.List;

public interface CommentService {

    public List<Comment> findById(Integer postId);
    public void saveComment(Integer postId, Comment comment);
    public void UpdateComment(Comment comment);
    public void deleteComment(Comment comment);
}
