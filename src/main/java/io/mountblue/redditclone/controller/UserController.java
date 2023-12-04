package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.entity.Role;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/")
public class UserController {
    UserService userService;

    @GetMapping("login")
    public String loginPage() {
        return "login";
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
}
