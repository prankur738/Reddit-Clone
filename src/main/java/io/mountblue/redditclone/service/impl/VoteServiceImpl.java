package io.mountblue.redditclone.service.impl;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.entity.VotePost;
import io.mountblue.redditclone.repository.PostRepository;
import io.mountblue.redditclone.repository.UserRepository;
import io.mountblue.redditclone.repository.VotePostRepository;
import io.mountblue.redditclone.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {
   private final VotePostRepository votePostRepository;
   private final PostRepository postRepository;
   private final UserRepository userRepository;

   @Autowired
   VoteServiceImpl(VotePostRepository votePostRepository, PostRepository postRepository, UserRepository userRepository){
       this.votePostRepository = votePostRepository;
       this.postRepository = postRepository;
       this.userRepository = userRepository;
   }
    @Override
    public Integer getChangeInVoteCount(String username, Integer postId, Integer voteCount) {
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


}
