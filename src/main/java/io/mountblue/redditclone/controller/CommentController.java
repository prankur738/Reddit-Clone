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

import java.security.Principal;
import java.sql.Timestamp;
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
        commentService.saveComment(postId, comment);
        System.out.println("comment User:"+ comment.getUser().getUsername());
        //Post post = postServiceImpl.findById(postId);
        List<Comment> commentList = post.getCommentList();

        model.addAttribute("post", post);
        model.addAttribute("commentList", commentList);
        return "redirect:/"+subredditName+"/posts/"+postId+"/comments";
    }

    @GetMapping("/{subredditName}/posts/{postId}/editComment/{commentId}")
    public String commentIdEdit(@PathVariable("subredditName")String subredditName,
                                @PathVariable("postId")Integer postId,
                                @PathVariable("commentId")Integer commentId,
                                Model model){
        Optional<Comment> editedComment = commentService.findById(commentId);
        Post post = postService.findById(postId);
        SubReddit subReddit = post.getSubReddit();
        List<Comment> commentList = post.getCommentList();
       boolean editMode = true;
        model.addAttribute("editMode", editMode);
        model.addAttribute("commentCount", commentList.size());
        model.addAttribute("post", post);
        model.addAttribute("postId", postId);
        model.addAttribute("editedComment",(Comment) editedComment.get());
        model.addAttribute("newComment", new Comment());
        model.addAttribute("commentList", commentList);
        model.addAttribute("subReddit", subReddit);
        model.addAttribute("commentId", commentId);
        model.addAttribute("subredditName", subredditName);
        return "viewPost";
    }

    @PostMapping("/{subredditName}/posts/{postId}/updateComment/{commentId}")
    public String editComment(@PathVariable("subredditName")String subredditName,
                              @PathVariable("postId")Integer postId,
                              @PathVariable("commentId")Integer commentId,
                              @ModelAttribute("editedComment") Comment editedComment,
                              Model model){
        Optional<Comment> updateComment = commentService.findById(commentId);
        updateComment.get().setText(editedComment.getText());
        updateComment.get().setId(commentId);
        commentService.saveComment(commentId, updateComment.get());
        boolean editMode = false;
        model.addAttribute("editMode", editMode);
        return "redirect:/"+subredditName+"/posts/"+postId+"/comments";
    }

    @PostMapping("/{subredditName}/posts/{postId}/deleteComment")
    public String deleteComment(@PathVariable("subredditName")String subredditName,
                                @PathVariable("postId")Integer postId,
                                @ModelAttribute("comment") Comment comment){
        commentService.deleteComment(comment);
        return "redirect:/"+subredditName+"/posts/"+postId+"/comments";
    }
}
