package com.example.appcore.repository;

import com.example.appcore.model.Enrollment;
import com.example.appcore.model.User;
import com.example.appcore.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    long countByStudentIdAndCompletedTrue(Long studentId);
}
