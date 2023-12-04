package io.mountblue.redditclone.service.impl;

import io.mountblue.redditclone.entity.VotePost;
import io.mountblue.redditclone.repository.VotePostRepository;
import io.mountblue.redditclone.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {
   private final VotePostRepository votePostRepository;

   @Autowired
   VoteServiceImpl(VotePostRepository votePostRepository){
       this.votePostRepository = votePostRepository;
   }
    @Override
    public Integer getChangeInVoteCount(String username, Integer postId, Integer voteCount) {
        Optional<VotePost> byUsernameAndPostId = votePostRepository.findByUsernameAndPostId(username, postId);

        if(byUsernameAndPostId.isPresent()){
            Integer oldVoteCount = byUsernameAndPostId.get().getVote();
            Integer newVoteCount = 0;

            if(oldVoteCount==0 || oldVoteCount==voteCount){
                newVoteCount = voteCount;
            }
            else{
                newVoteCount = 0;
            }

            byUsernameAndPostId.get().setVote(newVoteCount);
            votePostRepository.save(byUsernameAndPostId.get());

            return newVoteCount-oldVoteCount;
        }

        VotePost votePost = new VotePost(username,postId,voteCount);
        votePostRepository.save(votePost);
        return  voteCount;
    }
}
