package io.mountblue.redditclone.controller;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
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
                                             @PathVariable("communityName") String communityName,
                                             @AuthenticationPrincipal UserDetails userDetails){
        User user = userService.findByUsername(userDetails.getUsername());
        SubReddit subReddit = subRedditService.findByName(communityName);

        if (subReddit.getBannedUsers().contains(user)) {
            return "accessDenied";
        }

        model.addAttribute("post", new Post());
        model.addAttribute("subReddit", subReddit);

        return "createNewPost";
    }

    @PostMapping("/posts/savePost")
    public String processNewPost(@Valid @ModelAttribute("post") Post post,
                                 BindingResult bindingResult,
                                 @RequestParam("subRedditName") String subRedditName,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(name="tagNames", required = false) String tagNames,
                                 @RequestParam(value = "imageName",required = false) MultipartFile file,
                                 Model model) throws IOException {

        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        SubReddit subReddit = subRedditService.findByName(subRedditName);
        LocalDateTime startDate = LocalDateTime.now().minusHours(24);
        Integer postCountInLast24Hrs = postService.getPostsByUserInSubRedditInLast24Hours(username, subRedditName, startDate);
        Integer postLimit = subRedditService.findByName(subRedditName).getPostLimit();

        if(postCountInLast24Hrs >= postLimit){
            model.addAttribute("limitError", "Post limit exceeded for the day");
            model.addAttribute("post", new Post());
            model.addAttribute("allSubReddits", subRedditService.findAll());

            return "createNewPost";
        }

        if (subReddit.getBannedUsers().contains(user)) {
            return "accessDenied";
        }


        if (bindingResult.hasErrors()) {
            return "createNewPost";

        }


        if(file.isEmpty()){
            postService.createNewPost(post, subReddit.getId(), userDetails.getUsername(), tagNames);
        }
        else{
            String fileName = file.getOriginalFilename();
            java.io.File tempFile = java.io.File.createTempFile("temp", null);
            file.transferTo(tempFile.toPath()); // transfer data to io.File

            // Accessing serviceAccount file for firebase Authentication
            try (FileInputStream serviceAccount = new FileInputStream("./serviceAccountKey.json")) {
                //Creates a BlobId and BlobInfo for the file in firebase Storage.
                //BlobId is unique identifier of Blob object in firebase
                BlobId blobId = BlobId.of("reddit-clone-f5e1d.appspot.com", fileName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();

                //reading credentials from serviceAccount
                //uploading file content from temporary file to firebase
                Credentials credentials = GoogleCredentials.fromStream(serviceAccount);
                Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
                storage.create(blobInfo, Files.readAllBytes(tempFile.toPath()));

                String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/drive-db-415a1/o/%s?alt=media";

                //saving metadata in file entity

                post.setPhotoName(file.getOriginalFilename());
                post.setPhotoLink(String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8)));
                post.setPhotoSize(file.getSize());  // Set the file size
                post.setPhotoType(file.getContentType());


            } catch (IOException e) {
                // Handle the exception (log, throw, or return an error response)
                throw new RuntimeException("Error uploading file to Firebase Storage", e);
            } finally {
                // Delete the temporary file
                tempFile.delete();
            }

        }

        postService.createNewPost(post, subReddit.getId(), userDetails.getUsername(), tagNames);

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
        else{
            String fileName = file.getOriginalFilename();
            java.io.File tempFile = java.io.File.createTempFile("temp", null);
            file.transferTo(tempFile.toPath()); // transfer data to io.File

            // Accessing serviceAccount file for firebase Authentication
            try (FileInputStream serviceAccount = new FileInputStream("./serviceAccountKey.json")) {
                //Creates a BlobId and BlobInfo for the file in firebase Storage.
                //BlobId is unique identifier of Blob object in firebase
                BlobId blobId = BlobId.of("reddit-clone-f5e1d.appspot.com", fileName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();

                //reading credentials from serviceAccount
                //uploading file content from temporary file to firebase
                Credentials credentials = GoogleCredentials.fromStream(serviceAccount);
                Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
                storage.create(blobInfo, Files.readAllBytes(tempFile.toPath()));

                String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/drive-db-415a1/o/%s?alt=media";

                //saving metadata in file entity

                updatedPost.setPhotoName(file.getOriginalFilename());
                updatedPost.setPhotoLink(String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8)));
                updatedPost.setPhotoSize(file.getSize());  // Set the file size
                updatedPost.setPhotoType(file.getContentType());


            } catch (IOException e) {
                // Handle the exception (log, throw, or return an error response)
                throw new RuntimeException("Error uploading file to Firebase Storage", e);
            } finally {
                // Delete the temporary file
                tempFile.delete();
            }

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
                                  @RequestParam(name = "sort", defaultValue = "Top") String sort) throws IOException {
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

        for(Post post : allPosts){
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
        }

        model.addAttribute("allPosts", allPosts);
        model.addAttribute("subRedditList", subRedditList);
        model.addAttribute("sort", sort);
        model.addAttribute("bookmark",ids);

        return "homePage";
    }

    @GetMapping("/personalized-homepage")
    public String showHomepage(Model model,
                               @RequestParam(name = "sort", defaultValue = "Top") String sort,
                               @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User user = userService.findByUsername(userDetails.getUsername());
        List<Post> posts = postService.findAllBySubscribedSubReddits(user.getUsername());
        switch (sort) {
            case "Top" -> posts.sort((a, b) -> b.getVoteCount() - a.getVoteCount());
            case "Hot" -> posts.sort((a, b) -> b.getCommentList().size() - a.getCommentList().size());
            case "New" -> posts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            case "Old" -> posts.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        }
        for(Post post : posts){
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
