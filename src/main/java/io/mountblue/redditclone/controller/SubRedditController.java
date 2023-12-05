package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.service.PostService;
import io.mountblue.redditclone.service.SubRedditService;
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

import java.util.List;

@Controller
public class SubRedditController {

    SubRedditService subRedditService;
    PostService postService;
    @Autowired
    public SubRedditController(SubRedditService subRedditService, PostService postService) {
        this.subRedditService = subRedditService;
        this.postService = postService;
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
                                @PathVariable("subRedditName") String subRedditName){
        SubReddit subReddit = subRedditService.findByName(subRedditName);
        model.addAttribute("subReddit",subReddit);
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
}
