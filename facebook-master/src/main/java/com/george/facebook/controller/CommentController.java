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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProfileService profileService;


    // new comment
    @GetMapping
    public String newComment(Model model) {
        Profile profile = profileService.findById(getUserId());
        model.addAttribute("profile", profile);
        model.addAttribute("postId", 11);
        model.addAttribute("userId", profile.getId());
        model.addAttribute("comment", new Comment());
        return "comment/new-comment";
    }


    // save new comment
    @PostMapping("/save")
    public String saveComment(@Valid Comment comment, BindingResult bindingResult, Model model) {

        model.addAttribute("comment", new Comment());
        model.addAttribute("message", comment.getText());

        if (comment.getText().toString().length() < 1){
            return "redirect:/";
        }

        Comment newComment = commentService.save(comment);
        if (comment == null){
            return "redirect:/";
        }
//        return "redirect:/comment";
        return "redirect:/";
    }


    // delete post
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model, @RequestParam(name="p", defaultValue="1") int pageNumber){

        Comment comment = commentService.findById(id);
        commentService.deleteById(id);

        //
        List<Post> posts = postService.findAllDesc();
        int postCount = posts.size();
        model.addAttribute("postCount", postCount);
        //
        Page<Post> page = postService.getPage(pageNumber);
        model.addAttribute("page", page );
        model.addAttribute("pageNumber", pageNumber );

        //
        return "redirect:/posts";
    }






    // private methods
    //
    private boolean isAuthenticated(){
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (auth == "anonymousUser" )
            return false;
        return true;
    }

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


    //
}
