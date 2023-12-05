package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.Comment;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.service.PostService;
import io.mountblue.redditclone.service.SubRedditService;
import io.mountblue.redditclone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

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

    @GetMapping("/posts/createPost")
    public String showNewPostPage(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("allSubReddits", subRedditService.findAll());

        return "createNewPost";
    }

    @GetMapping("/community/{communityName}/createPost")
    public String showNewPostInSubredditPage(Model model,
                                             @PathVariable("communityName") String communityName){
        model.addAttribute("post", new Post());
        model.addAttribute("subReddit", subRedditService.findByName(communityName));

        return "createNewPost";
    }

    @PostMapping("/posts/savePost")
    public String processNewPost(@Valid @ModelAttribute("post") Post post,
                                 BindingResult bindingResult,
                                 @RequestParam("subRedditName") String subRedditName,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(name="tagNames", required = false) String tagNames,
                                 @RequestParam(value = "imageName",required = false
                                 ) MultipartFile file) throws IOException {
        if (bindingResult.hasErrors()) {
            return "createNewPost";

        }
        if(file.isEmpty()){
            SubReddit subReddit = subRedditService.findByName(subRedditName);
            postService.createNewPost(post, subReddit.getId(), userDetails.getUsername(), tagNames);

        }
        else  {
            post.setImage(file.getOriginalFilename());
            //path where image store
            File file1 = new ClassPathResource("static/image").getFile();
            Path path = Paths.get(file1.getAbsolutePath() + File.separator + file.getOriginalFilename());//create a path
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            SubReddit subReddit = subRedditService.findByName(subRedditName);
            postService.createNewPost(post, subReddit.getId(), userDetails.getUsername(), tagNames);

        }
        System.out.println(post.getImage());
        return "redirect:/community/" + subRedditName;
    }

    @GetMapping("/posts/{postId}")
    public String showFullPost(Model model, @PathVariable("postId") Integer postId, RedirectAttributes redirectAttributes){
        Post post = postService.findById(postId);
        model.addAttribute("post",post);
        redirectAttributes.addFlashAttribute("post", post);
        return "redirect:/posts/" + postId + "/comments";
    }

    @GetMapping("/posts/editPost/{postId}")
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

    @PostMapping("/posts/updatePost/{postId}")
    public String updatePost(@Valid @ModelAttribute("post") Post updatedPost,
                             BindingResult bindingResult,
                             @PathVariable("postId") Integer postId,
                             @RequestParam(name="tagNames", required = false) String tagNames,
                             @RequestParam(value = "imageName",required = false) MultipartFile file) throws IOException{
        if(bindingResult.hasErrors()){
            return "editPost";
        }

        if(file.isEmpty()){
            Post oldPost = postService.findById(postId);
            updatedPost.setId(postId);
            updatedPost.setCreatedAt(oldPost.getCreatedAt());
            updatedPost.setImage(oldPost.getImage());
            String username = oldPost.getUser().getUsername();
            postService.updatePost(updatedPost, updatedPost.getSubReddit().getId(),username,tagNames);
        }
        else  {
            Post oldPost = postService.findById(postId);
            updatedPost.setId(postId);
            updatedPost.setCreatedAt(oldPost.getCreatedAt());
            updatedPost.setImage(file.getOriginalFilename());
            //path where image store

            File file1 = new ClassPathResource("static/image").getFile();
            Path path = Paths.get(file1.getAbsolutePath() + File.separator + file.getOriginalFilename());//create a path
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String username = oldPost.getUser().getUsername();
            postService.updatePost(updatedPost, updatedPost.getSubReddit().getId(),username,tagNames);
        }
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/deletePost/{postId}")
    public String deletePost(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable("postId") Integer postId){

        boolean isUserAuthorized = postService.checkUserAuthorized(userDetails, postId);

        if(isUserAuthorized){
            postService.deletePost(postId);
            return "redirect:/user/" + userDetails.getUsername() + "/posts" ;
        }

        return "accessDenied";
    }

    @GetMapping("/")
    public String showHomePage(Model model){
        List<SubReddit> subRedditList = subRedditService.findAll();
        List<Post> allPosts = postService.findAllPosts();

        model.addAttribute("allPosts", allPosts);
        model.addAttribute("subRedditList", subRedditList);

        return "homePage";
    }

    @GetMapping("/personalized-homepage")
    public String showPersonalizedHomepage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        List<SubReddit> subRedditList = user.getSubRedditList();
        List<Post> posts = postService.findAllBySubscribedSubReddits(user.getUsername());

        model.addAttribute("allPosts", posts);
        model.addAttribute("subRedditList", subRedditList);
        model.addAttribute("personalized", true);

        return "homePage";
    }
}
