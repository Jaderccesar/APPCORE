package com.example.appcore.repository;

import com.example.appcore.model.Certificate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

  List<Certificate> findByCourseId(Long courseId); 
}
