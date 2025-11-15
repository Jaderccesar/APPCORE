package com.example.appcore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.appcore.model.Teacher;
import com.example.appcore.repository.TeacherRepository;

@Service
public class TeacherService {
  
  private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository repository) {
        this.teacherRepository = repository;
    }

    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher create(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Teacher update(Long id, Teacher updated) {
        return teacherRepository.findById(id)
                .map(existing -> {
                    existing.setSpecializedArea(updated.getSpecializedArea());
                    existing.setMinistredCourses(updated.getMinistredCourses());
                    existing.setChallenges(updated.getChallenges());
                    
                    return teacherRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Professor não encontrado: " + id));
    }

    public void delete(Long id) {
        teacherRepository.deleteById(id);
    }

}
