package com.george.facebook.controller;

import com.george.facebook.model.User;
import com.george.facebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.List;

@Controller
public class AuthController {

    Long oldUserId ;
    String oldEmail;


    @Autowired
    private UserService userService;

    //
    @GetMapping("/login")
    public String getLogin(Model model, User user, HttpServletRequest request){
        model.addAttribute("user", new User());
        // redirect an obj from another controller update
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        if(flashMap != null){
//            String emailId =  (String) flashMap.get("email");
//            System.out.println(emailId);
            model.addAttribute("reLogin", new User());
        }

        return "register/login";
    }

    //
    @PostMapping("/login")
    public String getin(){
        Long id = getUserId();
        User user = userService.findById(id);
        System.out.println(" ");
        System.out.println(" from auth controller login");
        System.out.println(" from auth controller login user password" + user.getPassword());
        return "post/posts";
    }

    //
    @GetMapping("/signup")
    public String getSignup(Model model){
        model.addAttribute("user", new User());
        return "register/signup";
    }

    //
    @PostMapping("/signup")
    public String registerUser(Model model, @Valid User user, BindingResult result){
        if (result.hasErrors()) {
//            model.addAttribute("user", new User());
            return "register/signup";
        }
        //
        User newUser = userService.save(user);
        if (newUser == null){
            model.addAttribute("invalidUser", user);
            return "register/signup";
        }
        return "redirect:/login";
    }

    @PostMapping("/update")
    public String userUpdate(Model model, @Valid User user, BindingResult result, RedirectAttributes redirectAttributes,
                             HttpServletRequest request, HttpServletResponse response){
//        if (result.hasErrors()) {
//            // redirect an obj to another controller profileController
//            redirectAttributes.addFlashAttribute("email", user.getEmail());
//            return "redirect:/profile/" + getUserId() ;
//        }

        oldUserId = getUserId();
        String oldEmail = userService.findById(oldUserId).getEmail().toString();

        boolean isEmailInUse = isEmailInUse(user.getEmail(), oldEmail);
        System.out.println(" user.getEmail() " + user.getEmail());
        System.out.println("oldEmail "+ oldEmail);
        System.out.println((user.getEmail()).equals(oldEmail));
        if (!isEmailInUse){
            redirectAttributes.addFlashAttribute("emailInUseError", user.getEmail());
            return "redirect:/profile/" + getUserId();
        }
        if (user.getEmail().length() < 4 ){
            redirectAttributes.addFlashAttribute("emailError", user.getEmail());
            return "redirect:/profile/" + getUserId();
        }
        if (user.getPassword().length() > 0) {
            if (!((user.getPassword()).equals(user.getVerifyPassword()))) {
                redirectAttributes.addFlashAttribute("passwordMarchError", user.getEmail());
                return "redirect:/profile/" + getUserId();
            }
        }


        User newUser = userService.update(user, getUserId());
        if (newUser == null){
            redirectAttributes.addFlashAttribute("passwordError", user.getEmail());
            return "redirect:/profile/" + getUserId();
        } else {
            // delete session after successfully changing pw or email
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            new SecurityContextLogoutHandler().logout(request, response, auth);
            // redirect an obj to another controller
            redirectAttributes.addFlashAttribute("reLogin", user.getEmail());
            return "redirect:/login";
        }
    }


    //
    @RequestMapping(value = "/delete_user/{id}", method = RequestMethod.GET)
    public String deleteUser(Model model, @PathVariable Long id) {
        Long userId = getUserId();
        User user = null;
        if (userId == id) {
            user = userService.delete(id);
        }
        if (user == null){
            return "redirect:/profile/" + userId;
        }

        return "redirect:/";
    }




    //
    @RequestMapping(value="/admin")
    public String adminInfo(ModelMap model, Authentication authentication) {
        model.addAttribute("name", authentication.getName());
        return "admin/admin";
    }

    //
    @RequestMapping(value="/user")
    public String userInfo(ModelMap model, Authentication authentication) {
        model.addAttribute("name", authentication.getName());
        return "post/posts";
    }

//    @Override
//    @RequestMapping(value="/error")
//    public String error() {
//        return "access-denied";
//    }




//           model.addAttribute("email", email);
//        System.out.println(" ");
//        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx ");
//        System.out.println("xxxx " + auth.getName());
//        System.out.println("xxxx " + auth.getDetails().toString())


    //
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

    // email already in use
    private Boolean isEmailInUse(String newEmail, String currentEmail){
        if (!(newEmail).equals(currentEmail) ) {
            User registeredEmail = userService.findByEmail(newEmail);
            if (registeredEmail != null) {
                return false;
            }
        }
        return true;
    }


}