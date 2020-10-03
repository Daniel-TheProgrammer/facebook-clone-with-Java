package com.george.facebook.controller;

import com.george.facebook.model.Comment;
import com.george.facebook.model.Post;
import com.george.facebook.model.Profile;
import com.george.facebook.model.User;
import com.george.facebook.service.CommentService;
import com.george.facebook.service.PostService;
import com.george.facebook.service.ProfileService;
import com.george.facebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class AjaxController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProfileService profileService;


    // save new comment
    //  @PostMapping("/api/save")
    @RequestMapping(value = "/save", produces = "application/json", method=RequestMethod.POST)
    public String postCustomer(@RequestBody Comment comment, Model model) {

        Comment newComment = commentService.apiSave(comment);
        // return statement
        if (comment != null) {
            return "Post Successfully!";
        }
        return "error";
    }


    // get my last comment
//    @GetMapping(value = "/new")
//    @GetMapping(value="/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(method = RequestMethod.GET, value = "/last/{id}")
    public Comment getMylastComent(@PathVariable Long id){
         Comment comment = commentService.getMylastComent(id);
        return comment;
    }



    // private methods
    private Long  getUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userService.findByEmail(email);
        if (user != null) {
            Long userId = user.getId();
            return userId;
        }
        return 0l;
    }




}
