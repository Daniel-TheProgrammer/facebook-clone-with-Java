package com.george.facebook.repository;

import com.george.facebook.model.Profile;
import com.george.facebook.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends CrudRepository<Profile, Long> {

    Profile findTopByOrderByIdDesc();
}
