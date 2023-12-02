package io.mountblue.redditclone.service.impl;

import io.mountblue.redditclone.entity.Comment;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.repository.CommentRepository;
import io.mountblue.redditclone.repository.PostRepository;
import io.mountblue.redditclone.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @Override
    public void saveComment(Integer postId, Comment comment){
        Optional<Post> optional = postRepository.findById(postId); //get comment post by id
        if (optional.isPresent()){
            comment.setPost(optional.get());
        }
        commentRepository.save(comment);
    }

    @Override
    public void UpdateComment(Comment comment) {
        Optional<Comment> optional = commentRepository.findById(comment.getId());
        if (optional.isPresent()) {
            Comment updatedComment = optional.get();
            updatedComment.setText(comment.getText());
            commentRepository.save(updatedComment);
        }
    }

    @Override
    public void deleteComment(Comment comment){
        commentRepository.delete(comment);
    }
}
