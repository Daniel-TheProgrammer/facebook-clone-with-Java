package com.george.facebook.repository;

import com.george.facebook.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
//public interface UserRepository extends CrudRepository<User, String>  {

    User findByEmail(String email);

    User findTopByOrderByIdDesc();

//    User findTop();
}
