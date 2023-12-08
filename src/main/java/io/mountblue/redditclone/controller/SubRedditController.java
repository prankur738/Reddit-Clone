package io.mountblue.redditclone.controller;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.mountblue.redditclone.entity.Bookmark;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.service.PostService;
import io.mountblue.redditclone.service.SubRedditService;
import io.mountblue.redditclone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
public class SubRedditController {

    SubRedditService subRedditService;
    PostService postService;
    UserService userService;
    @Autowired
    public SubRedditController(SubRedditService subRedditService, PostService postService, UserService userService) {
        this.subRedditService = subRedditService;
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/community/createCommunity")
    public String showNewSubRedditForm(Model model,
                                       @AuthenticationPrincipal UserDetails userDetails){
        model.addAttribute("subRedditList", subRedditService.findAll());
        model.addAttribute("subReddit", new SubReddit());
        return "newSubRedditForm";
    }

    @PostMapping("/community/saveCommunity")
    public String saveNewSubReddit(@Valid @ModelAttribute("subReddit") SubReddit  subReddit,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        subRedditService.createSubReddit(subReddit,username);
        return "redirect:/community/" + subReddit.getName();
    }


    @GetMapping("/community/{subRedditName}")
    public String showSubReddit(Model model,
                                @PathVariable("subRedditName") String subRedditName,
                                @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        SubReddit subReddit = subRedditService.findByName(subRedditName);
        List<Post> postList = subReddit.getPostList();
        for(Post post : postList){
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
        model.addAttribute("subReddit", subReddit);


        User users = userService.findByUsername(userDetails.getUsername());
        List<Bookmark> bookmarkList = users.getBookmarkList();
        List<Integer> ids = new ArrayList<>();

        for (Bookmark bookmark : bookmarkList) {
            ids.add(bookmark.getPost().getId());
        }
        model.addAttribute("bookmark", ids);
        if (subReddit.getSubscribers().contains(users)) {
            model.addAttribute("subscribedUser", true);

            if (userDetails != null) {
                User user = userService.findByUsername(userDetails.getUsername());

                boolean isAdmin = user.getId() == subReddit.getAdminUserId();
                boolean isMod = isAdmin || subReddit.getModerators().contains(user);

                model.addAttribute("isAdmin", isAdmin);
                model.addAttribute("isMod", isMod);

                if (subReddit.getSubscribers().contains(user)) {
                    model.addAttribute("subscribedUser", true);
                }

            }
        }

            model.addAttribute("admin", userService.findById(subReddit.getAdminUserId()));
            model.addAttribute("allUsers", userService.findAll());

            return "viewSubReddit";
        }
    

    @PostMapping("community/deleteCommunity/{subRedditId}")
    public String deleteSubReddit(@PathVariable("subRedditId") Integer subRedditId,
                                  @AuthenticationPrincipal UserDetails userDetails){
        boolean isUserAuthorized = subRedditService.checkUserAuthorized(userDetails, subRedditId);

        if(isUserAuthorized){
            subRedditService.deleteById(subRedditId);
            return "redirect:/";
        }

        else {
            return "accessDenied";
        }
    }

    @GetMapping("/community/editCommunity/{subRedditId}")
    public  String editSubReddit(@PathVariable("subRedditId") Integer subRedditId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        SubReddit subReddit = subRedditService.findById(subRedditId);
        boolean isUserAuthorized = subRedditService.checkUserAuthorized(userDetails, subRedditId);

        if(isUserAuthorized){
            model.addAttribute("subReddit",subReddit);
            model.addAttribute("subRedditId",subRedditId);

            return "editSubReddit";
        }

        return "accessDenied";
    }

    @PostMapping("/community/update")
    public String updateSubReddit(@Valid @ModelAttribute("subReddit") SubReddit  subReddit,
                                  BindingResult bindingResult,
                                  @AuthenticationPrincipal UserDetails userDetails){

        subRedditService.updateSubReddit(subReddit,userDetails.getUsername());
        return "redirect:/community/" + subReddit.getName();
    }

    @PostMapping("/community/{communityName}/join")
    public String joinCommunity(@PathVariable String communityName,
                                @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        subRedditService.addSubscriber(user, communityName);

        return "redirect:/community/" + communityName;
    }

    @PostMapping("/community/{communityName}/leave")
    public String leaveCommunity(@PathVariable String communityName,
                                @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        subRedditService.removeSubscriber(user, communityName);

        return "redirect:/community/" + communityName;
    }

    @PostMapping("/community/{communityName}/makeMod")
    public String makeModerator(@PathVariable String communityName, @RequestParam Integer userId) {
        User user = userService.findById(userId);

        subRedditService.makeMod(user, communityName);

        return "redirect:/community/" + communityName;
    }

    @PostMapping("/community/{communityName}/removeMod")
    public String removeModerator(@PathVariable String communityName, @RequestParam Integer userId) {
        User user = userService.findById(userId);

        subRedditService.removeMod(user, communityName);

        return "redirect:/community/" + communityName;
    }

    @PostMapping("/community/{communityName}/ban")
    public String banUser(@PathVariable String communityName, @RequestParam Integer userId) {
        User user = userService.findById(userId);
        System.out.println("test");

        subRedditService.banUser(user, communityName);

        return "redirect:/community/" + communityName;
    }

    @PostMapping("/community/{communityName}/unban")
    public String unbanUser(@PathVariable String communityName, @RequestParam Integer userId) {
        User user = userService.findById(userId);

        subRedditService.unbanUser(user, communityName);

        return "redirect:/community/" + communityName;
    }
}
