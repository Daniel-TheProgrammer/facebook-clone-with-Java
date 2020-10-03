package com.george.facebook.service;

import com.george.facebook.model.Profile;
import com.george.facebook.model.Avatar;
import com.george.facebook.model.User;
import com.george.facebook.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
public class ProfileService {


    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AvatarService avatarService;

    // save profile
    public Profile save(Profile profile, MultipartFile avatar ) {
        Long userId = getUserId();
        User user = userService.findById(userId);
        profile.setUser(user);
        profile.setId(userId);

        if (profile.getAdded()== null) {
            profile.setAdded(new Date());
        }
        Profile pro = profileRepository.save(profile);
        if (pro == null)
            return null;

        user.setProfile(pro);
        userService.save(user);

        // save avatar
        if (!avatar.isEmpty()) {
            Avatar ava = avatarService.save(avatar, pro);
            if (ava == null)
                return null;
        }

        return pro;
    }

    // find profile
    public Profile findById(Long id){
        Profile profile = null;
        try {
            profile = profileRepository.findById(id).get();
        } finally {
            if (profile == null)
                return null;
            return profile;
        }

    }

    // find the last profile
    public Long findTopByOrderByIdDesc() {
        Profile profile = profileRepository.findTopByOrderByIdDesc();
        Long id = profile.getId();
        return  id;
    }


    // private
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
//
//        if (file.isEmpty()) {
//        redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
//        return "redirect:uploadStatus";
//    }
//
//        try {
//
//        // Get the file and save it somewhere
//        byte[] bytes = file.getBytes();
//        Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
//        Files.write(path, bytes);
//
//        redirectAttributes.addFlashAttribute("message",
//                "You successfully uploaded '" + file.getOriginalFilename() + "'");
//
//    } catch (
//    IOException e) {
//        e.printStackTrace();
//    }
//
//        return "redirect:/uploadStatus";





//



//    public List<Profile> findAll() {
//        List<Profile> postList = new ArrayList<>();
//        Iterable<Profile> posts = postRepository.findAll();
//        posts.forEach(postList::add);
//        return postList;
//    }
//
//    public List<Profile> findAllDesc(){
//        return postRepository.findAllByOrderByAddedDesc();
//    }
////    public Page<StatusUpdate> getPage(int pageNumber) {
////        PageRequest request = PageRequest.of(pageNumber-1, PAGESIZE, Sort.Direction.DESC, "added");
////
////        return statusUpdateDao.findAll(request);
////    }
//
//    public Page<Profile> getPage(int pageNumber) {
//        PageRequest request = PageRequest.of(pageNumber-1, PAGESIZE, Sort.Direction.DESC, "added");
//
//        return postRepository.findAll(request);
//    }
//
//    public Profile findById(Long id) {
//        Profile profile = null;
//        try {
//            profile = postRepository.findById(id).get();
//        } finally {
//
//            if(profile == null){
//                profile = postRepository.findTopByOrderByIdDesc();
//            }
//
//            return profile;
//        }
//
//    }
//
//    public void deleteById(Long id) {
//        postRepository.deleteById(id);
//    }
//
//
//    public List<Profile> findAllByUserId(Long id) {
//        List<Profile> postList = new ArrayList<>();
////        Iterable<Profile> posts = postRepository.findAllByUserId(id);
////        repository.findAll(Sort.by(Sort.Direction.DESC, "colName"));
//        Iterable<Profile> posts = postRepository.findAllByUserIdOrderByIdDesc(id);
//        posts.forEach(postList::add);
//        return postList;
//    }




    public String getE(String error){
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        System.out.println( error);
        return " ";
    }



    //
}
