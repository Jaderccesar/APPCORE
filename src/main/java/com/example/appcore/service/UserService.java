package com.example.appcore.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.example.appcore.enums.AccountType;
import com.example.appcore.enums.Gender;
import com.example.appcore.model.Student;
import com.example.appcore.model.Teacher;
import com.example.appcore.model.User;
import com.example.appcore.repository.StudentRepository;
import com.example.appcore.repository.TeacherRepository;

@Service
public class UserService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public User createUserFromMap(Map<String, Object> data) {

      String accountType = data.get("accountType").toString().toUpperCase();

      switch (accountType) {

        case "STUDENT": {
          Student s = new Student();
          s.setName((String) data.get("name"));
          s.setEmail((String) data.get("email"));
          s.setPassword((String) data.get("password"));
          s.setCpf((String) data.get("cpf"));
          s.setBirthday(LocalDate.parse((String) data.get("birthday")));
          s.setGenero(Gender.valueOf((String) data.get("genero")));
          s.setAccountType(AccountType.STUDENT);
          return studentRepository.save(s);
        }

        case "TEACHER": {
          Teacher t = new Teacher();
          t.setName((String) data.get("name"));
          t.setEmail((String) data.get("email"));
          t.setPassword((String) data.get("password"));
          t.setCpf((String) data.get("cpf"));
          t.setBirthday(LocalDate.parse((String) data.get("birthday")));
          t.setGenero(Gender.valueOf((String) data.get("genero")));
          t.setAccountType(AccountType.TEACHER);
          t.setSpecializedArea((String) data.get("specializedArea"));
          return teacherRepository.save(t);
        }

        default:
          throw new IllegalArgumentException("Tipo inválido: " + accountType);
      }
    }
    
    public User login(String email, String password) {

    // tenta primeiro como estudante
    Optional<Student> student = studentRepository.findByEmail(email);
    if (student.isPresent()) {
        if (student.get().getPassword().equals(password)) {
            return student.get();
        }
        throw new IllegalArgumentException("Senha incorreta");
    }

    // tenta como professor
    Optional<Teacher> teacher = teacherRepository.findByEmail(email); 
    if (teacher.isPresent()) {
        if (teacher.get().getPassword().equals(password)) {
            return teacher.get();
        }
        throw new IllegalArgumentException("Senha incorreta");
    }

    throw new IllegalArgumentException("Usuário não encontrado");
    }
}