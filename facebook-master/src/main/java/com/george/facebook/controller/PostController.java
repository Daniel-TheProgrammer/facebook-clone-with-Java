package com.george.facebook.controller;

import com.george.facebook.model.*;
import com.george.facebook.service.AvatarService;
import com.george.facebook.service.PostService;
import com.george.facebook.service.ProfileService;
import com.george.facebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
//@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UserService userService;
    @Autowired
    private AvatarService avatarService;

    // get all post
    @RequestMapping("/")
    public String home(Model model, RedirectAttributes redirectAttributes) {
//        // check if they have a username
        if (isAuthenticated()) {
          if (!hasAProfile(getUserId())) {
              redirectAttributes.addFlashAttribute("noProfile", "no user profile");
              return "redirect:/profile/new";
          }
        }

        List<Post> posts = postService.findAllDesc();
        int postCount = posts.size();
        model.addAttribute("posts", posts);
        model.addAttribute("postCount", postCount);
        //
//        Avatar avatar = avatarService.findLast(id);
//        if (avatar.getPath() != null) {
//            List<Avatar> avatars = avatarService.findAllByProfileId(id);
//            model.addAttribute("avatars", avatars);
//
//            Avatar lastAvatar = avatarService.findLast(id);
//            model.addAttribute("lastAvatar", lastAvatar);
//        }
        //
        Long userId = getUserId();
        model.addAttribute("user", profileService.findById(userId));
        model.addAttribute("userId", userId);
        // comment
        model.addAttribute("comment", new Comment());
        return "post/all-posts";
    }

    // all pagination posts
    @RequestMapping("/posts")
    public String home(Model model, @RequestParam(name="p", defaultValue="1") int pageNumber) {
        List<Post> posts = postService.findAllDesc();
        int postCount = posts.size();
//        model.addAttribute("posts", posts);
        model.addAttribute("postCount", postCount);
        //
        Page<Post> page = postService.getPage(pageNumber);
        model.addAttribute("page", page );
        model.addAttribute("pageNumber", pageNumber );

        //
        Long userId = getUserId();
        model.addAttribute("userId", userId);
        // comment
        model.addAttribute("comment", new Comment());

        return "post/posts";
    }


    // addpost page
    @RequestMapping("/addpost")
    public String addPost(Model model) {
        Long userId = getUserId();
        model.addAttribute("userId", userId);
        model.addAttribute("post", new Post());
        return "post/addpost";
    }


    // save
    @PostMapping("/addpost")
    // also in model & form
    public String addPost(@Valid Post post, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            return "post/addpost";
        }

        Post newPost = postService.save(post);
        if (post == null){
            return "post/addpost";
        }
        return ("redirect:/posts");
    }


    // get single post
    @GetMapping("/post/{id}")
    public String getPost(Model model, @PathVariable Long id){
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        //
        Long userId = getUserId();
        model.addAttribute("userId", userId);
        // comment
        model.addAttribute("comment", new Comment());

        return "post/post";
    }


    // edit post
    @GetMapping("/editpost/{id}")
    public String editPost(Model model, @PathVariable Long id){
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        //
        Long userId = getUserId();
        model.addAttribute("userId", userId);
        // comment
        model.addAttribute("comment", new Comment());

        return "post/editpost";
    }


    // save edit post
    @PostMapping("/editpost/{id}")
    public String editPost(@Valid Post post, BindingResult bindingResult, Model model, @PathVariable Long id){
        String text = post.getText().trim();

        if (bindingResult.hasErrors()) {
            return "redirect:/post/" + id;
        }

        if (text.length()< 1) {
            return "redirect:/post/" + id;
        }

        Post newPost = postService.save(post, id);
        if (post == null){
            return "redirect:/post/" + id;
        }
        return "redirect:/post/" + id;
    }


    // delete post
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model, @RequestParam(name="p", defaultValue="1") int pageNumber){
        Post post = postService.findById(id);
        postService.deleteById(id);

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

    private boolean hasAProfile(Long id){
        Profile profile = profileService.findById(id);
        if (profile == null){
            return false;
        }
        return true;
    }


    //
}
