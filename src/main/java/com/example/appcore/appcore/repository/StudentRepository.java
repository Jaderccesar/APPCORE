package com.example.appcore.appcore.repository;

import com.example.appcore.appcore.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {}
