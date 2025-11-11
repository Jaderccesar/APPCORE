package com.example.appcore.appcore.service;

import com.example.appcore.appcore.enums.AccountType;
import com.example.appcore.appcore.enums.Status;
import com.example.appcore.appcore.model.Address;
import com.example.appcore.appcore.model.Student;
import com.example.appcore.appcore.repository.AddressRepository;
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

    @Autowired
    private AddressRepository addressRepository;

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Student save(Student student) {
        Address address = student.getAddress();

        if (address != null) {
            if (address.getId() != null) {
                Address existingAddress = addressRepository.findById(address.getId())
                        .map(existing -> {
                            existing.setStreet(address.getStreet());
                            existing.setCity(address.getCity());
                            existing.setState(address.getState());
                            existing.setZip(address.getZip());
                            existing.setCountry(address.getCountry());
                            existing.setComplement(address.getComplement());
                            existing.setNumber(address.getNumber());
                            return addressRepository.save(existing);
                        })
                        .orElseGet(() -> addressRepository.save(address));

                student.setAddress(existingAddress);
            } else {
                Address savedAddress = addressRepository.save(address);
                student.setAddress(savedAddress);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        student.setCreatedAt(now);
        student.setUpdatedAt(now);
        student.setStatus(Status.ACTIVE);

        if (student.getAccountType() == null) {
            student.setAccountType(AccountType.STUDENT);
        }

        return studentRepository.save(student);
    }

    public Student update(Long id, Student student) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado com o id: " + id));

        existingStudent.setName(student.getName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setCpf(student.getCpf());
        existingStudent.setBirthday(student.getBirthday());
        existingStudent.setGenero(student.getGenero());
        existingStudent.setFavoriteLanguage(student.getFavoriteLanguage());
        existingStudent.setAccountType(student.getAccountType());
        existingStudent.setTotalScore(student.getTotalScore());
        existingStudent.setNivel(student.getNivel());
        existingStudent.setUpdatedAt(LocalDateTime.now());

        Address newAddress = student.getAddress();
        Address existingAddress = existingStudent.getAddress();

        if (newAddress != null) {
            if (existingAddress != null) {
                existingAddress.setStreet(newAddress.getStreet());
                existingAddress.setCity(newAddress.getCity());
                existingAddress.setState(newAddress.getState());
                existingAddress.setZip(newAddress.getZip());
                existingAddress.setCountry(newAddress.getCountry());
                existingAddress.setComplement(newAddress.getComplement());
                existingAddress.setNumber(newAddress.getNumber());
            } else {
                existingStudent.setAddress(newAddress);
            }
        } else {
            existingStudent.setAddress(null);
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
