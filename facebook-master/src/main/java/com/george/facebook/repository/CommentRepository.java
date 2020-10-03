package com.george.facebook.repository;

import com.george.facebook.model.Comment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {


    Comment findFirstByOrderByAddedDesc();

    List<Comment> findAllByOrderByAddedDesc();

    Comment findTopByOrderByIdDesc();

    Comment findTopByProfileIdOrderByIdDesc(Long id);

//    Iterable<Comment> findAllByProfileId(Long id);

    Iterable<Comment> findAllByProfileIdOrderByIdDesc(Long id);

    List<Comment> findAllByProfileId(Long id);




    //
}
