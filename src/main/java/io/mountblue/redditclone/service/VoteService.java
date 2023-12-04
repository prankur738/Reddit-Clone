package io.mountblue.redditclone.service;

public interface VoteService {
    Integer getChangeInVoteCount(String username,Integer postId, Integer voteCount);
}
