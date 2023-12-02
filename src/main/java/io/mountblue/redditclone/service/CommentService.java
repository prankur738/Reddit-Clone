package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.Comment;

public interface CommentService {
    public void saveComment(Integer postId, Comment comment);
    public void UpdateComment(Comment comment);
    public void deleteComment(Comment comment);
}
