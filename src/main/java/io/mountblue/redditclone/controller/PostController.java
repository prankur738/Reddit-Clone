package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.Comment;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.service.PostService;
import io.mountblue.redditclone.service.SubRedditService;
import io.mountblue.redditclone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final SubRedditService subRedditService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class,trimmerEditor);
    }

    @Autowired
    PostController(PostService postService, UserService userService, SubRedditService subRedditService){
        this.postService = postService;
        this.userService = userService;
        this.subRedditService = subRedditService;
    }

    @GetMapping("/r/{subRedditName}/createPost")
    public String showNewPostPage(Model model,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  @PathVariable("subRedditName") String subRedditName){

        if(userDetails == null){ // non-logged in user
            return "accessDenied";
        }

        model.addAttribute("post", new Post());
        model.addAttribute("subRedditName", subRedditName);

        return "createNewPost";
    }

    @PostMapping("/savePost")
    public String processNewPost(@Valid @ModelAttribute("post") Post post,
                                 BindingResult bindingResult,
                                 @RequestParam("subRedditName") String subRedditName,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(name="tagNames") String tagNames){

        if(bindingResult.hasErrors()){
            return "createNewPost";
        }
        else{

            SubReddit subReddit = subRedditService.findByName(subRedditName);
            postService.createNewPost(post,subReddit.getId(), userDetails.getUsername(), tagNames);
            return "redirect:/r/" + subRedditName ;
        }
    }

    @GetMapping("/r/{subRedditName}/{postId}")
    public String showFullPost(Model model,
                               @PathVariable("postId") Integer postId){
        Post post = postService.findById(postId);
        model.addAttribute("post",post);
        model.addAttribute("newComment", new Comment());

        return "viewPost";
    }

    @GetMapping("/r/{subRedditName}/editPost/{postId}")
    public String showEditPostPage(Model model,
                                   @PathVariable("postId") Integer postId,
                                   @AuthenticationPrincipal UserDetails userDetails){

        Post post = postService.findById(postId);

        boolean isUserAuthorized = postService.checkUserAuthorized(userDetails, postId);

        if(isUserAuthorized){
            String tagNames = postService.getCommaSeperatedTags(post.getId());

            model.addAttribute("post",post);
            model.addAttribute("tagNames", tagNames);

            return "editPost";
        }

        return "accessDenied";
    }

    @PostMapping("/r/{subRedditName}/updatePost/{postId}")
    public String updatePost(@Valid @ModelAttribute("post") Post updatedPost,
                             BindingResult bindingResult,
                             @PathVariable("postId") Integer postId,
                             @PathVariable("subRedditName") String subRedditName,
                             @RequestParam(name="tagNames", required = false) String tagNames){

        if(bindingResult.hasErrors()){
            return "editPost";
        }
        else{
            SubReddit subReddit = subRedditService.findByName(subRedditName);
            String username = postService.findById(postId).getUser().getUsername();
            postService.updatePost(updatedPost, subReddit.getId(),username,tagNames);
            return "redirect:/r/" + subRedditName + "/" + postId;
        }
    }

    @PostMapping("/r/{subRedditName}/deletePost/{postId}")
    public String deletePost(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable("postId") Integer postId,
                             @PathVariable("subRedditName") String subRedditName) {

        boolean isUserAuthorized = postService.checkUserAuthorized(userDetails, postId);

        if(isUserAuthorized){
            postService.deletePost(postId);
            return "redirect:/r/"+ subRedditName ;
        }

        return "accessDenied";
    }

    @GetMapping("/")
    public String showHomePage(Model model){
        List<SubReddit> subRedditList = subRedditService.findAll();
        model.addAttribute("subRedditList",subRedditList);
        return "homePage";
    }


}
