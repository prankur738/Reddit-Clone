package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.service.CommentService;
import io.mountblue.redditclone.service.PostService;
import io.mountblue.redditclone.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VoteController {
    private final VoteService voteService;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    VoteController(VoteService voteService, PostService postService, CommentService commentService){
        this.voteService = voteService;
        this.postService = postService;
        this.commentService = commentService;
    }
    @PostMapping("/processPostVote")
    public String processVoteCountOnPost(@RequestParam("vote") Integer vote,
                                         @RequestParam("postId") Integer postId,
                                         @RequestParam("endPoint") String endPoint,
                                         @RequestParam(name = "sort", defaultValue = "Top") String sort,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        int voteCountChange = voteService.getChangeInVotePostCount(username,postId,vote );

        postService.updatePost(postId, voteCountChange);

        return "redirect:" + endPoint + "?sort=" + sort;
    }

    @PostMapping("/processCommentVote")
    public String processVoteCountOnComment(@RequestParam("vote") Integer vote,
                                            @RequestParam("postId") Integer postId,
                                            @RequestParam("commentId") Integer commentId,
                                            @AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        int voteCountChange = voteService.getChangeInVoteCommentCount(username, postId, commentId, vote);

        commentService.updateComment(commentId,voteCountChange);

        String subRedditName = postService.findById(postId).getSubReddit().getName();

        return "redirect:/" + subRedditName + "/posts/" + postId + "/comments";
    }
}
