package com.example.appcore.appcore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Enterprise extends JpaRepository<Enterprise, Long> {}
