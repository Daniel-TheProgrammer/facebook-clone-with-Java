package com.george.facebook.service;

import com.george.facebook.model.Post;
import com.george.facebook.model.Profile;
import com.george.facebook.model.User;
import com.george.facebook.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private final static int PAGESIZE = 3;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProfileService profileService;

    // new post
    public Post save(Post post) {
        Profile profile = profileService.findById(getUserId());
        post.setProfile(profile);
        postRepository.save(post);
        return post;
    }

    // update
    public Post save(Post post, Long id) {
        Post myPost = findById(id);
        myPost.setId(id);
        myPost.setText(post.getText());
        myPost.setAdded(myPost.getAdded());
        return  save(myPost);
    }

    // delete
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public List<Post> findAllDesc(){
        return postRepository.findAllByOrderByAddedDesc();
    }
//    public Page<StatusUpdate> getPage(int pageNumber) {
//        PageRequest request = PageRequest.of(pageNumber-1, PAGESIZE, Sort.Direction.DESC, "added");
//
//        return statusUpdateDao.findAll(request);
//    }

    public Page<Post> getPage(int pageNumber) {
        PageRequest request = PageRequest.of(pageNumber-1, PAGESIZE, Sort.Direction.DESC, "added");

        return postRepository.findAll(request);
    }

    public List<Post> findAllByUserId(Long id) {
        List<Post> postList = new ArrayList<>();
        Iterable<Post> posts = postRepository.findAllByProfileIdOrderByIdDesc(id);
        posts.forEach(postList::add);
        return postList;
    }


    public List<Post> findAll() {
        List<Post> postList = new ArrayList<>();
        Iterable<Post> posts = postRepository.findAll();
        posts.forEach(postList::add);
        return postList;
    }

    // here

    public Post findById(Long id) {
        Post post = null;
        try {
            post = postRepository.findById(id).get();
        } finally {

            if(post == null){
                post = postRepository.findTopByOrderByIdDesc();
            }

            return post;
        }

    }





    // private methods

    public String getE(String error){
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        System.out.println( error);
        return " ";
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
