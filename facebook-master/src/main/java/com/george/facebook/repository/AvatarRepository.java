package com.george.facebook.repository;

import com.george.facebook.model.Avatar;
import com.george.facebook.model.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository extends CrudRepository<Avatar, Long> {

    Iterable<Avatar> findAllByProfileIdOrderByIdDesc(Long id);

    Avatar findTopByProfileIdOrderByIdDesc(Long id);

}
