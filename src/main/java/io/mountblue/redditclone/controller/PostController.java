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
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    public String processNewPost( @ModelAttribute("post") Post post,
                                 @RequestParam("subRedditName") String subRedditName,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(name="tagNames") String tagNames,
                                 @RequestParam("imageName") MultipartFile file) throws IOException {
//        if(bindingResult.hasErrors()){
//            System.out.println("1");
//            return "createNewPost";
//        }
//        else {
            if (file.isEmpty()) {
                System.out.println("2");
                System.out.println("file empty");
            }
            System.out.println(file.getOriginalFilename());
                post.setImage(file.getOriginalFilename());
                //path where image store
                File file1 = new ClassPathResource("static/css/image").getFile();

                Path path = Paths.get(file1.getAbsolutePath() + File.separator + file.getOriginalFilename());//create a path
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                SubReddit subReddit = subRedditService.findByName(subRedditName);
                postService.createNewPost(post, subReddit.getId(), userDetails.getUsername(), tagNames);
                return "redirect:/r/" + subRedditName;
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
