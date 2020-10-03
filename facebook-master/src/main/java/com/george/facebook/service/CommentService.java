package com.george.facebook.service;

import com.george.facebook.model.Comment;
import com.george.facebook.model.Post;
import com.george.facebook.model.Profile;
import com.george.facebook.model.User;
import com.george.facebook.repository.CommentRepository;
import com.george.facebook.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    private final static int PAGESIZE = 3;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private PostService postService;


    // save new comment
    public Comment save(Comment comment) {
        Profile profile = profileService.findById(getUserId());
        comment.setProfile(profile);
        comment.setPost(comment.getPost());
        Comment newComment = commentRepository.save(comment);
        return newComment;
    }


    // delete comment
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }



    // list of comments
    public List<Comment> findAllDesc(){
        return commentRepository.findAllByOrderByAddedDesc();
    }



    public Comment apiSave(Comment comment) {
        Profile profile = profileService.findById(getUserId());
        Post post = postService.findById(comment.getPostId());

        comment.setProfile(profile);
        comment.setPost(post);

        commentRepository.save(comment);
        return comment;
    }


    // find the last item
//    public Long findTopByOrderByIdDesc() {
//        User user = userRepository.findTopByOrderByIdDesc();
//        Long id = user.getId();
//        return  id;
//    }

    // find the last comment
    public Comment findTopByOrderByIdDesc() {
        Comment comment = commentRepository.findTopByOrderByIdDesc();
        return  comment;
    }

    // find my last comment
    public Comment getMylastComent(Long id) {
        Comment comment = commentRepository.findTopByProfileIdOrderByIdDesc(id);
        Comment myComment = new Comment();
        myComment.setText(comment.getText());
        myComment.setId(comment.getId());
        myComment.setAdded(comment.getAdded());
        myComment.setUserId(getUserId());
        myComment.setPostId(comment.getPost().getId());
        return myComment;
    }







    // private methods

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

    public Comment findById(Long id) {
        Comment comment = commentRepository.findById(id).get();
        return comment;
    }

    public List<Comment> getAll() {
        List<Comment> commentList = new ArrayList<>();
        Iterable<Comment> comments = commentRepository.findAll();
        comments.forEach(commentList::add);
        return commentList;
    }




//
}
