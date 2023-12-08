package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.Comment;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.repository.CommentRepository;
import io.mountblue.redditclone.service.CommentService;
import io.mountblue.redditclone.service.PostService;
import io.mountblue.redditclone.service.SubRedditService;
import io.mountblue.redditclone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class CommentController {

    PostService postService;

    CommentService commentService;

    UserService userService;

    SubRedditService subRedditService;

    CommentRepository commentRepository;

    @Autowired
    public CommentController(PostService postService, CommentService commentService, UserService userService, SubRedditService subRedditService, CommentRepository commentRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
        this.subRedditService = subRedditService;
        this.commentRepository = commentRepository;
    }


    @GetMapping("/{subredditName}/posts/{postId}/comments")
    public String showComments(@PathVariable("subredditName")String subredditName, Model model, @PathVariable("postId")Integer postId){

        Post post = postService.findById(postId);
        SubReddit subReddit = post.getSubReddit();
        List<Comment> commentList = post.getCommentList();
        Boolean editMode = false;

        model.addAttribute("commentCount", commentList.size());
        model.addAttribute("post", post);
        model.addAttribute("postId", postId);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("commentList", commentList);
        model.addAttribute("subReddit", subReddit);
        model.addAttribute("subredditName", subredditName);
        model.addAttribute("editMode", editMode);
        model.addAttribute("commentId",model.getAttribute("commentId"));
        model.addAttribute("commentbody",model.getAttribute("commentbody"));

        return "viewPost";
    }

    @PostMapping("/{subredditName}/posts/{postId}/saveComment")
    public String createAndSaveComment(@PathVariable("subredditName")String subredditName,
                                       @PathVariable("postId")Integer postId,
                                       @ModelAttribute("newComment") Comment comment,
                                       @ModelAttribute("post")Post post,
                                       Model model, Principal principal){
        User user =userService.findByUsername(principal.getName());
        comment.setUser(user);
        Post post1 = postService.findById(postId);
        commentService.saveComment(postId, comment,post1.getUser());
        System.out.println("comment User:"+ comment.getUser().getUsername());
        //Post post = postServiceImpl.findById(postId);
        List<Comment> commentList = post.getCommentList();

        model.addAttribute("post", post);
        model.addAttribute("commentList", commentList);
        return "redirect:/"+subredditName+"/posts/"+postId+"/comments";
    }

    @GetMapping("/comment/{postId}/{commentId}/edit")
    public String editCommentForm(@PathVariable("postId") Integer postId,
                                  @PathVariable("commentId") Integer commentId,
                                  Model model, RedirectAttributes redirectAttributes){
        System.out.println("Hello");

        Optional<Comment> comment = commentService.findById(commentId);
        Post post = postService.findById(postId);
        SubReddit subreddit = post.getSubReddit();
        String subredditName = subreddit.getName();

        model.addAttribute("commentId",comment.get().getId());
        model.addAttribute("commentbody",comment.get().getText());
        redirectAttributes.addFlashAttribute("commentId",comment.get().getId());
        redirectAttributes.addFlashAttribute("commentbody",comment.get().getText());

        return "redirect:/"+subredditName+"/posts/"+postId+"/comments";

    }
    @PostMapping("/comment/{postId}/{commentId}")
    public String editComment( @ModelAttribute("comments") Comment comment,
                               @PathVariable("postId") Integer postId,
                               @PathVariable("commentId") Integer commentId,
                               @RequestParam("updateContent") String editComment,
                               Model model){
        System.out.println(editComment);
        Post post = postService.findById(postId);
        SubReddit subreddit = post.getSubReddit();
        String subredditName = subreddit.getName();
        commentService.updateComment(commentId,editComment,postId);



        return "redirect:/"+subredditName+"/posts/"+postId+"/comments";
    }




    @GetMapping("/comment/{postId}/{commentId}/delete")
    public String deleteComment(@PathVariable("commentId") Integer commentId,
                                @PathVariable("postId") Integer postId){
        commentService.deleteComment(commentId);
        Post post = postService.findById(postId);
        SubReddit subreddit = post.getSubReddit();
        String subredditName = subreddit.getName();


        return "redirect:/"+subredditName+"/posts/"+postId+"/comments";
    }

}
