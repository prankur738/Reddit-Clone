package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.Comment;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.service.CommentService;
import io.mountblue.redditclone.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@Controller
public class CommentController {

    PostService postService;
    CommentService commentService;

    @Autowired
    public CommentController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public String showComments(Model model, @PathVariable("postId")Integer postId){

        Post post = postService.findById(postId);
        List<Comment> commentList = post.getCommentList();

        model.addAttribute("post", post);
        model.addAttribute("postId", postId);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("commentList", commentList);

        return "viewPost";
    }

    @PostMapping("/posts/{postId}/saveComment")
    public String createAndSaveComment(Model model,@PathVariable("postId")Integer postId, @ModelAttribute("newComment") Comment comment, @ModelAttribute("post")Post post){
        System.out.println("comment get:"+comment.getText());
        commentService.saveComment(postId, comment);

        //Post post = postServiceImpl.findById(postId);
        List<Comment> commentList = post.getCommentList();

        model.addAttribute("post", post);
        model.addAttribute("commentList", commentList);
        return "redirect:/posts/" + postId ;
    }

    @PostMapping("/comment/update")
    public String editComment(@RequestParam Integer postId,@ModelAttribute("comment") Comment comment){
        commentService.UpdateComment(comment);
        return "";
    }

    @PostMapping("/comment/delete")
    public String deleteComment(@ModelAttribute("comment") Comment comment){
        commentService.deleteComment(comment);
        return "";
    }
}
