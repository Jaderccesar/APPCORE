package com.example.appcore.repository;

import com.example.appcore.model.Telephone;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelephoneRepository extends JpaRepository<Telephone, Long> {
  List<Telephone> findByUserId(Long userId); 
}
