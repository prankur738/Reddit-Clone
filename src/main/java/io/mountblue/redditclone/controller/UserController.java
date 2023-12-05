package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.Comment;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@RequestMapping("/")
public class UserController {
    UserService userService;

    @GetMapping("login")
    public String loginPage(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails == null ? "login" : "redirect:/";
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("signup")
    public String register(Model model) {
        model.addAttribute("newUser", new User());

        return "signup";
    }

    @PostMapping("/signup/validate")
    public String processRegistration(@Valid @ModelAttribute("newUser") User newUser,
                                      BindingResult bindingResult,
                                      @RequestParam(required = false) String confirmPassword,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        if (!newUser.getPassword().equals(confirmPassword)) {
            model.addAttribute("newUser", newUser);
            model.addAttribute("registrationError", "Passwords do not match");

            return "signup";
        }

        boolean userAlreadyExists = userService.findByUsername(newUser.getUsername()) != null;
        if (userAlreadyExists){
            model.addAttribute("newUser", newUser);
            model.addAttribute("registrationError",
                    "Username already exists. Enter a different username");

            return "signup";
        }

        userService.encodePassword(newUser);
        userService.save(newUser);
        userService.grantRoleToUser(newUser.getUsername(), "USER");

        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "accessDenied";
    }

    @GetMapping("user/{username}/{action}")
    public String showProfilePage(Model model,
                                      @PathVariable("username") String username,
                                      @PathVariable("action") String action){
        User user = userService.findByUsername(username);
        List<Post> posts1 = user.getPosts();


        if(action.equals("comments")){
            List<Comment> comments = user.getComments();
            model.addAttribute("comment", comments);


        } else if (action.equals("community")) {
            List<SubReddit> subReddits = user.getSubRedditList();
            model.addAttribute("communities",subReddits);

        } else {
            List<Post> posts = null;

            if(action.equals("posts")){
                posts = user.getPosts();
            }
            else if(action.equals("upVoted")){
                posts = userService.getUpVotedPosts(username);
            }
            else if(action.equals("downVoted")){
                posts = userService.getDownVotedPosts(username);
            }

            model.addAttribute("posts",posts);

        }
        model.addAttribute("action",action);
        return "profilePage";

    }
}
