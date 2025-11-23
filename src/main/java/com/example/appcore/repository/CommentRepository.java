package com.example.appcore.repository;

import com.example.appcore.model.Comment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  Optional<Comment> findByAuthorIdAndCourseId(Long authorId, Long courseId);

  List<Comment> findByCourseId(Long courseId);

  @Query("SELECT c FROM Comment c WHERE c.author.id = :userId AND c.course.id = :courseId AND c.rating IS NOT NULL")
  Optional<Comment> findByAuthorIdAndCourseIdAndRatingNotNull(Long userId, Long courseId);

  List<Comment> findByCourseIdAndParentIsNull(Long courseId);
}
