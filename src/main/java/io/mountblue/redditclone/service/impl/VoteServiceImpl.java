package io.mountblue.redditclone.service.impl;

import io.mountblue.redditclone.entity.*;
import io.mountblue.redditclone.repository.*;
import io.mountblue.redditclone.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {
    private final VotePostRepository votePostRepository;
    private final VoteCommentRepository voteCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    VoteServiceImpl(VotePostRepository votePostRepository, PostRepository postRepository,
                    UserRepository userRepository, CommentRepository commentRepository,
                    VoteCommentRepository voteCommentRepository){
        this.votePostRepository = votePostRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.voteCommentRepository = voteCommentRepository;
    }
    @Override
    public Integer getChangeInVotePostCount(String username, Integer postId, Integer voteCount) {
        User user = userRepository.findByUsername(username);
        Post post = postRepository.findById(postId).get();
        Optional<VotePost> votePostOptional = votePostRepository.findAByUserAndPost(user, post);

        if(votePostOptional.isPresent()){
            Integer oldVoteCount = votePostOptional.get().getVote();
            Integer newVoteCount = 0;

            if(oldVoteCount==0 || oldVoteCount==voteCount){
                newVoteCount = voteCount;
            }
            else{
                newVoteCount = 0;
            }

            votePostOptional.get().setVote(newVoteCount);
            votePostRepository.save(votePostOptional.get());

            return newVoteCount-oldVoteCount;
        }

        VotePost votePost = new VotePost(user, post, voteCount);
        votePostRepository.save(votePost);
        return  voteCount;
    }

    @Override
    public Integer getChangeInVoteCommentCount(String username, Integer postId, Integer commentId, Integer voteCount) {
        User user = userRepository.findByUsername(username);
        Post post = postRepository.findById(postId).get();
        Comment comment = commentRepository.findById(commentId).get();

        Optional<VoteComment> voteCommentOptional = voteCommentRepository.findByUserAndComment(user, comment);

        if(voteCommentOptional.isPresent()){
            System.out.println("Already present");
            Integer oldVoteCount = voteCommentOptional.get().getVote();
            Integer newVoteCount = 0;

            if(oldVoteCount==0 || oldVoteCount==voteCount){
                newVoteCount = voteCount;
            }
            else{
                newVoteCount = 0;
            }

            voteCommentOptional.get().setVote(newVoteCount);
            voteCommentRepository.save(voteCommentOptional.get());

            return newVoteCount-oldVoteCount;
        }

        System.out.println("Making new Vote comment object");

        VoteComment voteComment = new VoteComment(user, post, comment, voteCount);
        voteCommentRepository.save(voteComment);
        return  voteCount;
    }

}
