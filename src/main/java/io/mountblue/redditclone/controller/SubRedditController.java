package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.Bookmark;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
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
                                @AuthenticationPrincipal UserDetails userDetails){
        SubReddit subReddit = subRedditService.findByName(subRedditName);
        model.addAttribute("subReddit",subReddit);

        User user = userService.findByUsername(userDetails.getUsername());
        List<Bookmark> bookmarkList = user.getBookmarkList();
        List<Integer> ids = new ArrayList<>();

        for(Bookmark bookmark: bookmarkList) {
            ids.add(bookmark.getPost().getId());
        }
        model.addAttribute("bookmark",ids);
        if (subReddit.getSubscribers().contains(user)) {
            model.addAttribute("subscribedUser", true);
        }

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
}
