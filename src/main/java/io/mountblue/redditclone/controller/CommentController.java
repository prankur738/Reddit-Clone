package io.mountblue.redditclone.controller;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
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
    public String showComments(@PathVariable("subredditName")String subredditName, Model model,
                               @PathVariable("postId")Integer postId,
                               @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        Post post = postService.findById(postId);

        if(post.getPhotoName()!= null){
            String fileName = post.getPhotoName();
            // Download file from Firebase Storage
            Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("./serviceAccountKey.json"));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            Blob blob = storage.get(BlobId.of("reddit-clone-f5e1d.appspot.com", fileName));

            String contentType = post.getPhotoType();
            String base64Image = Base64.getEncoder().encodeToString(blob.getContent());

            post.setPhotoType(contentType);
            post.setImage(base64Image);
        }

        SubReddit subReddit = post.getSubReddit();
        List<Comment> commentList = post.getCommentList();
        Boolean editMode = false;

        List<Comment> commentsOrderByVotesDesc = commentService.getAllByPostIdOrderByVotesDesc(postId);

        model.addAttribute("commentCount", commentList.size());
        model.addAttribute("post", post);
        model.addAttribute("postId", postId);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("commentList", commentsOrderByVotesDesc);
        model.addAttribute("subReddit", subReddit);
        model.addAttribute("subredditName", subredditName);
        model.addAttribute("editMode", editMode);
        model.addAttribute("commentId",model.getAttribute("commentId"));
        model.addAttribute("commentbody",model.getAttribute("commentbody"));

        boolean isAuthor = false, isMod = false;
        if (userDetails != null) {
            User currentUser = userService.findByUsername(userDetails.getUsername());
            isAuthor = post.getUser().getId() == currentUser.getId();
            isMod = subReddit.getModerators().contains(currentUser);
        }

        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("isMod", isMod);

        return "viewPost";
    }

    @PostMapping("/{subredditName}/posts/{postId}/saveComment")
    public String createAndSaveComment(@PathVariable("subredditName")String subredditName,
                                       @PathVariable("postId")Integer postId,
                                       @ModelAttribute("newComment") Comment comment,
                                       @ModelAttribute("post")Post post,
                                       Model model, Principal principal){
        User user =userService.findByUsername(principal.getName());
        SubReddit subReddit = subRedditService.findByName(subredditName);

        if (subReddit.getBannedUsers().contains(user)) {
            return "accessDenied";
        }

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
