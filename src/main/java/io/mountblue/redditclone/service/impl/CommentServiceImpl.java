package io.mountblue.redditclone.service.impl;

import io.mountblue.redditclone.entity.Comment;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.repository.CommentRepository;
import io.mountblue.redditclone.repository.PostRepository;
import io.mountblue.redditclone.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @Override
    public List<Comment> findByPostId(Integer postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    public Optional<Comment> findById(Integer postId){
        return commentRepository.findById(postId);
    }

    @Override
    public void saveComment(Integer postId, Comment comment, User postOwner){
        Optional<Post> optional = postRepository.findById(postId); //get comment post by id
        if (optional.isPresent()){
            comment.setPost(optional.get());
        }
        if(!comment.getText().isEmpty()){
            if(postOwner.getKarma() == null)
                postOwner.setKarma(1);
            else
                postOwner.setKarma(postOwner.getKarma()+1);
            commentRepository.save(comment);
        }
    }

    @Override
    public void UpdateComment(Integer commentId,String editComment,Integer postId) {

        Optional<Comment> comment = commentRepository.findById(commentId);
        comment.get().setText(editComment);
        commentRepository.save(comment.get());

    }

    @Override
    public void deleteComment(Integer commentId){
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<Comment> findCommentsBySearchQuery(String query) {
        return commentRepository.getCommentsBySearch(query);
    }
}
