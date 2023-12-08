package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.*;
import io.mountblue.redditclone.service.CommentService;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.List;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final SubRedditService subRedditService;
    private final CommentService commentService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class,trimmerEditor);
    }

    @Autowired
    PostController(PostService postService, UserService userService,
                   SubRedditService subRedditService, CommentService commentService){
        this.postService = postService;
        this.userService = userService;
        this.subRedditService = subRedditService;
        this.commentService = commentService;
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

        String username = userDetails.getUsername();
        LocalDateTime startDate = LocalDateTime.now().minusHours(24);
        Integer postCountInLast24Hrs = postService.getPostsByUserInSubRedditInLast24Hours(username, subRedditName, startDate);
        if(postCountInLast24Hrs >=4){
            return "accessDenied";
        }
        System.out.println(postCountInLast24Hrs);

        if (bindingResult.hasErrors()) {
            return "createNewPost";

        }

        User user = userService.findByUsername(username);
        List<Post> posts = user.getPosts();
        if(posts.size()>3){
            return "accessDenied";
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
                             @AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable("postId") Integer postId,
                             @RequestParam(name="tagNames", required = false) String tagNames,
                             @RequestParam(value = "imageName",required = false) MultipartFile file) throws IOException{
        if(bindingResult.hasErrors()){
            return "editPost";
        }

        if(file.isEmpty()){
            Post oldPost = postService.findById(postId);
            updatedPost.setImage(oldPost.getImage());
        }
        else  {
            updatedPost.setImage(file.getOriginalFilename());
            //path where image store

            File file1 = new ClassPathResource("static/image").getFile();
            Path path = Paths.get(file1.getAbsolutePath() + File.separator + file.getOriginalFilename());//create a path
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        postService.updatePost(updatedPost, postId ,tagNames);
        String username = userDetails.getUsername();

        return "redirect:/user/" + username + "/posts";
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
    public String showPopularPage(Model model,@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(name = "sort", defaultValue = "Top") String sort){
        List<Post> allPosts = switch (sort) {
            case "Top" -> postService.findAllOrderByVoteCountDesc();
            case "Hot" -> postService.findAllPostsOrderedByCommentsSizeDesc();
            case "New" -> postService.findAllOrderByCreatedAtDesc();
            case "Old" -> postService.findAllOrderByCreatedAt();
            default -> postService.findAllPosts();
        };

        List<Integer> ids = new ArrayList<>();
        if(userDetails != null){
            User user = userService.findByUsername(userDetails.getUsername());
            List<Bookmark> bookmarkList = user.getBookmarkList();

            for(Bookmark bookmark: bookmarkList) {
                ids.add(bookmark.getPost().getId());
            }
        }

        List<SubReddit> subRedditList = subRedditService.findAll();

        model.addAttribute("allPosts", allPosts);
        model.addAttribute("subRedditList", subRedditList);
        model.addAttribute("sort", sort);
        model.addAttribute("bookmark",ids);

        return "homePage";
    }

    @GetMapping("/personalized-homepage")
    public String showHomepage(Model model,
                               @RequestParam(name = "sort", defaultValue = "Top") String sort,
                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        List<Post> posts = postService.findAllBySubscribedSubReddits(user.getUsername());
        switch (sort) {
            case "Top" -> posts.sort((a, b) -> b.getVoteCount() - a.getVoteCount());
            case "Hot" -> posts.sort((a, b) -> b.getCommentList().size() - a.getCommentList().size());
            case "New" -> posts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            case "Old" -> posts.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        }

        List<SubReddit> subRedditList = subRedditService.findAll();

        List<Bookmark> bookmarkList = user.getBookmarkList();
        List<Integer> ids = new ArrayList<>();

        for(Bookmark bookmark: bookmarkList) {
            ids.add(bookmark.getPost().getId());
        }

        model.addAttribute("bookmark",ids);
        model.addAttribute("allPosts", posts);
        model.addAttribute("subRedditList", subRedditList);
        model.addAttribute("sort", sort);
        model.addAttribute("personalized", true);

        return "homePage";
    }

    @GetMapping("/search/{action}")
    public String showSearchResultPage(Model model,@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestParam(value = "query", required = false) String query,
                                       @PathVariable("action") String action){
        if(action.equals("posts")){
            List<Post> posts = postService.findPostsBySearchQuery(query);
            model.addAttribute("allPosts", posts);
        }
        else if(action.equals("community")){
            List<SubReddit> communities = subRedditService.findSubRedditsBySearchQuery(query);
            model.addAttribute("communities", communities);
        }
        else if(action.equals("comments")){
            List<Comment> comments = commentService.findCommentsBySearchQuery(query);
            model.addAttribute("comments",comments);
        }

        User user = userService.findByUsername(userDetails.getUsername());
        List<Bookmark> bookmarkList = user.getBookmarkList();
        List<Integer> ids = new ArrayList<>();

        for(Bookmark bookmark: bookmarkList) {
            ids.add(bookmark.getPost().getId());
        }

        List<SubReddit> subRedditList = subRedditService.findAll();
        model.addAttribute("subRedditList",subRedditList);

        model.addAttribute("action", action);
        model.addAttribute("query", query);
        model.addAttribute("bookmark",ids);
        return "searchResult";
    }
}
