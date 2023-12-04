package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.Comment;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.repository.CommentRepository;
import io.mountblue.redditclone.service.CommentService;
import io.mountblue.redditclone.service.impl.CommentServiceImpl;
import io.mountblue.redditclone.service.impl.PostServiceImpl;
import io.mountblue.redditclone.service.impl.SubRedditServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    SubRedditServiceImpl subRedditServiceImpl;

    @Autowired
    PostServiceImpl postServiceImpl;

    @Autowired
    CommentServiceImpl commentServiceImpl;

    @GetMapping("/posts/{postId}/comments")
    public String showComments(Model model, @PathVariable("postId")Integer postId){

        Post post = postServiceImpl.findById(postId);
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
        commentServiceImpl.saveComment(postId, comment);

        //Post post = postServiceImpl.findById(postId);
        List<Comment> commentList = post.getCommentList();

        model.addAttribute("post", post);
        model.addAttribute("commentList", commentList);
        return "redirect:/posts/" + postId ;
    }

    @PostMapping("/comment/update")
    public String editComment(@RequestParam Integer postId,@ModelAttribute("comment") Comment comment){
        commentServiceImpl.UpdateComment(comment);
        return "";
    }

    @PostMapping("/comment/delete")
    public String deleteComment(@ModelAttribute("comment") Comment comment){
        commentServiceImpl.deleteComment(comment);
        return "";
    }
}
