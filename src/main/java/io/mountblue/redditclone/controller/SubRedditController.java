package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.SubReddit;
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

@Controller
public class SubRedditController {

    SubRedditService subRedditService;
    @Autowired
    public SubRedditController(SubRedditService subRedditService) {
        this.subRedditService = subRedditService;
    }

    @GetMapping("/createNewSubreddit")
    public String showNewSubRedditForm(Model model,
                                       @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails == null){ // non-logged in user
            return "accessDenied";
        }

        model.addAttribute("subReddit", new SubReddit());
        return "newSubRedditForm";
    }

    @PostMapping("/saveSubReddit")
    public String saveNewSubReddit(@Valid @ModelAttribute("subReddit") SubReddit  subReddit,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        subRedditService.createSubReddit(subReddit,username);
        return "redirect:/r/" + subReddit.getName();
    }


    @GetMapping("/r/{subRedditName}")
    public String showSubReddit(Model model,
                                @PathVariable("subRedditName") String subRedditName){
        SubReddit subReddit = subRedditService.findByName(subRedditName);

        model.addAttribute("subReddit",subReddit);

        return "viewSubReddit";
    }

    @PostMapping("/deleteSubReddit/{subRedditId}")
    public String deleteSubReddit(@PathVariable("subRedditId") Integer subRedditId,
                                  @AuthenticationPrincipal UserDetails userDetails){
        boolean isUserAuthorized = subRedditService.checkUserAuthorized(userDetails, subRedditId);

        if(isUserAuthorized){
            subRedditService.deleteById(subRedditId);
            return "";
        }

        else {
            return "accessDenied";
        }
    }

    @GetMapping("/editSubReddit/{subRedditId}")
    public  String editsubReddit(@PathVariable("subRedditId") Integer subRedditId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        SubReddit subReddit = subRedditService.findById(subRedditId);
        boolean isUserAuthorized = subRedditService.checkUserAuthorized(userDetails, subRedditId);

        if(isUserAuthorized){
            model.addAttribute("subReddit",subReddit);
            model.addAttribute("subRedditId",subRedditId);

            return "";
        }

        return "accessDenied";

    }

    @PostMapping("/updateSubReddit")
    public String updateSubReddit(@Valid @ModelAttribute("subReddit") SubReddit  subReddit,
                                  BindingResult bindingResult,
                                  @AuthenticationPrincipal UserDetails userDetails){

        subRedditService.updateSubReddit(subReddit,userDetails.getUsername());
        return "";
    }
}
