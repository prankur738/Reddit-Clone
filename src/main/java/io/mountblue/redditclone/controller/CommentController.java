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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/comments")
    public String createAndSaveComment(@RequestParam("pid")Integer postId,
                                       @ModelAttribute("comment") Comment comment){
        commentServiceImpl.saveComment(postId,comment);
        return "";
    }

    @PostMapping("/commentsShow/{subRedditId}/{postId}")
    public String showComments(@RequestParam("subRedditId")Integer subRedditId, @RequestParam("postId")Integer postId, Model model, RedirectAttributes redirectAttributes){
        SubReddit subReddit= subRedditServiceImpl.findById(subRedditId);
        List<Post> post = postServiceImpl.findBySubRedditId(subReddit.getId());
        List<Comment> commentList = null;
        for(Post postList : post)
            if(postList.getId().equals(postId))
                commentList = commentServiceImpl.findById(postId);

        redirectAttributes.addFlashAttribute("commentsCounts", commentList.size());
        redirectAttributes.addFlashAttribute("commentsList", commentList);
        return "";
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
