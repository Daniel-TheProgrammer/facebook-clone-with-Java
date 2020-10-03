package com.george.facebook.controller;

import com.george.facebook.model.*;
import com.george.facebook.service.AvatarService;
import com.george.facebook.service.PostService;
import com.george.facebook.service.ProfileService;
import com.george.facebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContextUtils;
//
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = System.getProperty("java.io.tmpdir");

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AvatarService avatarService;

    // profile home page
    @GetMapping("/{id}")
    public String getProfile(Model model, @PathVariable Long id, HttpServletRequest request){
        // loggein in user
        if (isAuthenticated()) {
            Long userId = getUserId();
            model.addAttribute("userId", userId);

            User user = userService.findById(userId);
            model.addAttribute("user", user);
            user.setPassword("");
        }

        // profile visited
        Profile profile = profileService.findById(id);
        if (profile == null) {
            id = profileService.findTopByOrderByIdDesc();
            profile = profileService.findById(id);
        }
        model.addAttribute("profile", profile);

        Profile bio = profileService.findById(id);
        model.addAttribute("bio", bio);

        List<Post> posts = postService.findAllByUserId(id);
//        List<Post> posts = postService.findAllDesc();
        int postCount = posts.size();
        model.addAttribute("posts", posts);
        model.addAttribute("postCount", postCount);

        Avatar avatar = avatarService.findLast(id);
        if (avatar.getPath() != null) {
            List<Avatar> avatars = avatarService.findAllByProfileId(id);
            model.addAttribute("avatars", avatars);

            Avatar lastAvatar = avatarService.findLast(id);
            model.addAttribute("lastAvatar", lastAvatar);
        }

        // comment
        model.addAttribute("comment", new Comment());


        // redirect an obj from another controller AuthController
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        if(flashMap != null){
            String emailError = (String) flashMap.get("emailError");
            String emailInUseError = (String) flashMap.get("emailInUseError");
            String passwordError = (String) flashMap.get("passwordError");
            String passwordMarchError = (String) flashMap.get("passwordMarchError");
            String noProfile = (String) flashMap.get("noProfile");
//            i like the top better
//            model.addAttribute("emailError", user.getEmail());
//            model.addAttribute("emailInUseError", user.getEmail());
//            model.addAttribute("passwordError", user.getEmail());
        }
        return "profile/profile";
    }


    // get profile
    @GetMapping("/new")
    public String newProfile(Model model, @Valid Profile profile, BindingResult bindingResult, HttpServletRequest request){
        Long usedId = getUserId();
        model.addAttribute("bio", new Profile());
        //
        // redirect an obj from another controller AuthController
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        if(flashMap != null) {
            String noProfile = (String) flashMap.get("noProfile");
        }
        return "profile/edit-profile";
    }

    // edit profile
    @GetMapping("/edit/{id}")
    public String editUser(Model model,@PathVariable Long id ){
        Long usedId = getUserId();
        if (usedId != id)
            id = usedId;
        User user = userService.findById(id);
        user.setPassword("");
        model.addAttribute("user", user);
        model.addAttribute("id", id);

        Long userId = getUserId();
        model.addAttribute("userId", userId);

        Profile profile = profileService.findById(id);
        model.addAttribute("profile", profile);
        // comment
        model.addAttribute("comment", new Comment());

        return "profile/edit-profile";
    }


    // save profile
    @PostMapping
    public String postProfile(Model model, Profile profile, @RequestParam("avatar") MultipartFile avatar){
        // save profile
        Long usedId = getUserId();
        Profile newProfile = profileService.save(profile, avatar);

        if (newProfile == null) {
            return "redirect:/profile/" + usedId;
        }

        return "redirect:/profile/" + usedId;
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


//    // upload start
//    @PostMapping("/upload") // //new annotation since 4.3
//    public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
//        profileService.saveFile(file);
////        System.out.println(" ");
////        System.out.println(" singleFileUpload " + file.getName());
//        Long usedId = getUserId();
//        return "/profile/edit/" + usedId;
//    }

// upload start

//    // return a new page
//    @RequestMapping(value = "/create", method = RequestMethod.POST)
//    public String usercreate(User user, RedirectAttributes attr){
//        attr.addFlashAttribute("users", user);
//        return "redirect:/user/showuser";
//    }
//
//    @RequestMapping(value ="/showuser")
//    public String showUser(User user){
//        return "user";
//    }

