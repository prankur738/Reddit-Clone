package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.Comment;
import io.mountblue.redditclone.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    public List<Comment> findByPostId(Integer postId);

    public Optional<Comment> findById(Integer postId);

    public void saveComment(Integer postId, Comment comment, User user);
  
    public void updateComment(Integer commenId,String editComment,Integer postId);

    public void deleteComment(Integer commentId);
  
    List<Comment> findCommentsBySearchQuery(String query);

    void updateComment(Integer commentId, Integer voteCountChange);

    List<Comment> getAllByPostIdOrderByVotesDesc(Integer postId);
}
