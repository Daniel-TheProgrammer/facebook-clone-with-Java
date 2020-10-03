package com.george.facebook.repository;

import com.george.facebook.model.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    Post findFirstByOrderByAddedDesc();

    List<Post> findAllByOrderByAddedDesc();

    Post findTopByOrderByIdDesc();

//    Iterable<Post> findAllByUserId(Long id);

//    Iterable<Post> findAllByUserIdOrderByIdDesc(Long id);

    Iterable<Post> findAllByProfileId(Long id);

    Iterable<Post> findAllByProfileIdOrderByIdDesc(Long id);


    //
}
