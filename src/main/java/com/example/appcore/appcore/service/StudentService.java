package com.example.appcore.appcore.service;

import com.example.appcore.appcore.enums.AccountType;
import com.example.appcore.appcore.enums.Status;
import com.example.appcore.appcore.model.Student;
import com.example.appcore.appcore.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Student save(Student student) {
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        student.setStatus(Status.ACTIVE);
        student.setAccountType(AccountType.ENTERPRISE); //provisorio e padrão precisa revisar criação de um nivel admin
        return studentRepository.save(student);
    }

    public Student update(Long id, Student student) {
        Student existingStudent = studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Estudante não econtrado com o id: " + id));

        existingStudent.setName(student.getName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setCpf(student.getCpf());
        existingStudent.setBirthday(student.getBirthday());
        existingStudent.setGenero(student.getGenero());
        existingStudent.setFavoriteLanguage(student.getFavoriteLanguage());
        existingStudent.setAccountType(student.getAccountType());
        existingStudent.setTotalScore(student.getTotalScore());
        existingStudent.setNivel(student.getNivel());
        
        // Atualizar endereço se fornecido
        if (student.getAddress() != null) {
            if (existingStudent.getAddress() != null) {
                // Atualiza campos do endereço existente
                existingStudent.getAddress().setStreet(student.getAddress().getStreet());
                existingStudent.getAddress().setCity(student.getAddress().getCity());
                existingStudent.getAddress().setState(student.getAddress().getState());
                existingStudent.getAddress().setZip(student.getAddress().getZip());
                existingStudent.getAddress().setCountry(student.getAddress().getCountry());
                existingStudent.getAddress().setComplement(student.getAddress().getComplement());
                existingStudent.getAddress().setNumber(student.getAddress().getNumber());
            } else {
                // Cria novo endereço se não existir
                existingStudent.setAddress(student.getAddress());
            }
        }

        return studentRepository.save(existingStudent);
    }
    
    public Optional<Student> findByEmail(String email) {
        return studentRepository.findAll().stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }
}
