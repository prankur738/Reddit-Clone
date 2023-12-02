package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.Comment;
import io.mountblue.redditclone.repository.CommentRepository;
import io.mountblue.redditclone.service.CommentService;
import io.mountblue.redditclone.service.impl.CommentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentServiceImpl commentServiceImpl;

    @GetMapping("/comments")
    public String createAndSaveComment(@RequestParam("pid")Integer postId, @ModelAttribute("comment") Comment comment){
        commentServiceImpl.saveComment(postId,comment);
        return "";
    }

    @PostMapping("/comment/update")
    public String editComment(@RequestParam Integer postId, @ModelAttribute("comment") Comment comment){
        commentServiceImpl.UpdateComment(comment);
        return "";
    }

    @PostMapping("/comment/delete")
    public String deleteComment(@ModelAttribute("comment") Comment comment){
        commentServiceImpl.deleteComment(comment);
        return "";
    }
}
