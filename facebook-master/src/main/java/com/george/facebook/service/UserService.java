package com.george.facebook.service;

import com.george.facebook.model.User;
import com.george.facebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    private Object myAuthenticationManager = null;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null ) {
            throw new UsernameNotFoundException("email not found");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getAuthority());
        UserDetails userDetails = (UserDetails)new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(), Arrays.asList(authority));

        return userDetails;
    }



    public User save(User user) {
        if (user.getAuthority() == null)
            user.setAuthority("ROLE_USER");
        if (user.getEnabled() == 0)
            user.setEnabled(1);

        // passwords dont match
        if(!(user.getPassword().equals(user.getConfirmPassword()))){
            return null;
        }

        // dont store confrimedPassword
        user.setConfirmPassword("");

        // email already in use
        User myUser = userRepository.findByEmail(user.getEmail());
        if (myUser != null ) {
            return null;
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }



    // update
    public User update(User user, Long userId) {
        // current logged in user info
        User currentUser = findById(userId);
        String currentEmail = currentUser.getEmail();

        if (user.getAuthority() == null)
            user.setAuthority("ROLE_USER");
        if (user.getEnabled() == 0)
            user.setEnabled(1);


        // email logic
        String newEmail = user.getEmail();
        if (newEmail.length() < 4 ){
            return null;
        } else {
            //
            String regex = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(newEmail);
            if (!matcher.matches()) {
                return null;
            }
        }

        // already registered
        if (!(newEmail).equals(currentEmail) ) {
            User registeredEmail = findByEmail(newEmail);
            if (registeredEmail != null) {
                return null;
            }
        }

        // check current password
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getAuthority());
        UserDetails userDetails = (UserDetails) new org.springframework.security.core.userdetails.User(currentUser.getEmail(), user.getVerifyPassword(), Arrays.asList(authority));
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, user.getVerifyPassword(), userDetails.getAuthorities());
        try {
            myAuthenticationManager = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } finally {
        }


        // passwords logic match, length
        boolean march = false;
        if (user.getPassword().length() > 0) {
            if (user.getPassword().length() < 6)
                return null;

            march = (user.getPassword().toString()).equals(user.getConfirmPassword().toString());
            if (march != true)
                return null;


            user.setConfirmPassword("");
            user.setVerifyPassword(" ");
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            currentUser.setPassword(encodedPassword);
        }


        currentUser.setEmail(user.getEmail());
        currentUser.setId(userId);
        currentUser.setAuthority(user.getAuthority());
        currentUser.setEnabled(user.getEnabled());
        //
        return userRepository.save(currentUser);
    }




    public User findByEmail(String email) {
        User user = null;
        return  userRepository.findByEmail(email);

    }

    public User findById(Long userId) {
        User user = null;
        try {
            user = userRepository.findById(userId).get();
        } finally {
            if (user == null)
                return user;
        }
        return user;
    }


    public String printError(String error ){
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        System.out.println(" ");
        System.out.println("              " + error);
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        return "";
    }

    // find the last item
    public Long findTopByOrderByIdDesc() {
        User user = userRepository.findTopByOrderByIdDesc();
        Long id = user.getId();
        return  id;
    }

    public User delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
//        System.out.println(" ");
//        System.out.println(" delete user service " + id);
        return user;
    }


//
}
