package com.george.facebook.service;

import com.george.facebook.model.Post;
import com.george.facebook.model.Profile;
import com.george.facebook.model.Avatar;
import com.george.facebook.repository.AvatarRepository;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class AvatarService {
//
    private static String imageFolder = System.getProperty("user.dir")+"/src/main/resources/static/uploads/";

    @Autowired
    private AvatarRepository avatarRepository;


    // return all avatars
    public List<Avatar> findAllByProfileId(Long id) {
        List<Avatar> avatarList = new ArrayList<>();
        Iterable<Avatar> avatars = avatarRepository.findAllByProfileIdOrderByIdDesc(id);
        avatars.forEach(avatarList::add);
        return avatarList;
    }

    // return last avatar
    public Avatar findLast(Long id) {
        List<Avatar>  avatarList = findAllByProfileId( id);

        if (avatarList.size()> 0){
            // find first element
            Avatar first = avatarList.get(0);
            // find last element
            Avatar last = avatarList.get(avatarList.size() - 1);

            return first;
        }
        return new Avatar();
    }



    // save avatar
    public Avatar save(MultipartFile avatar, Profile profile) {

        Avatar profileAvatar = createAvatar(profile);
        // Get the file and save it somewhere
        byte[] bytes = new byte[0];
        try {
            bytes = avatar.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path path = Paths.get(imageFolder + avatarPathName(profileAvatar, avatar ));

        profileAvatar.setPath(avatarPathName(profileAvatar, avatar ));
        avatarRepository.save(profileAvatar);

        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }

        return profileAvatar;
    }











    // methods
    private Avatar createAvatar(Profile profile){
        Avatar profileAva = new Avatar();
        profileAva.setProfile(profile);
        return avatarRepository.save(profileAva);
    }

    private String avatarPathName(Avatar profileAvatar, MultipartFile avatar ){
        StringBuilder avatarName  = new StringBuilder();
        avatarName.append(profileAvatar.getProfile().getUser().getId());
        avatarName.append(profileAvatar.getId());
        avatarName.append(avatar.getOriginalFilename());
        return avatarName.toString();
    }


    //
}

//    public void saveFile(MultipartFile file) {
//            // Get the file and save it somewhere
//            byte[] bytes = new byte[0];
////        String imageFolder = "/uploads";
////        System.out.println(" imageFolder " + imageFolder.toString());
////        byte[] bytes = new byte[0];
//            try {
//                bytes = file.getBytes();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Path path = Paths.get(imageFolder + file.getOriginalFilename());
////        System.out.println( "path "+ path);
//            try {
//                Files.write(path, bytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//    }




