package io.mountblue.redditclone.controller;

import io.mountblue.redditclone.repository.UserRepository;
import io.mountblue.redditclone.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookmarkController {

    BookmarkService bookmarkService;

    UserRepository userRepository;
    @Autowired
    public BookmarkController(BookmarkService bookmarkService,UserRepository  userRepository) {
        this.bookmarkService = bookmarkService;
        this.userRepository = userRepository;
    }
    @GetMapping("/bookmark/{postId}/{endPoint}")
    public String bookmarkPost(@PathVariable Integer postId,
                               @PathVariable(value = "endPoint", required = false) String endPoint,
                               @AuthenticationPrincipal UserDetails userDetails){

        bookmarkService.bookmarkPost(postId,userDetails.getUsername());
        if(endPoint.equals("home")){
            return "redirect:/";
        }
        else if(endPoint.equals("posts")){
            return  "redirect:/user/"+userDetails.getUsername()+'/'+endPoint;
        }

            return "redirect:/community/"+endPoint;
    }
}
