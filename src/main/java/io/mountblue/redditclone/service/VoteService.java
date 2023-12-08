package io.mountblue.redditclone.service;

public interface VoteService {
    Integer getChangeInVotePostCount(String username, Integer postId, Integer voteCount);
    Integer getChangeInVoteCommentCount(String username, Integer postId, Integer commentId, Integer voteCount);
}
