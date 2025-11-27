package com.example.appcore.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.example.appcore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.example.appcore.enums.AccountType;
import com.example.appcore.enums.Gender;
import com.example.appcore.model.Post;
import com.example.appcore.model.Student;
import com.example.appcore.model.Teacher;
import com.example.appcore.model.User;
import com.example.appcore.repository.StudentRepository;
import com.example.appcore.repository.TeacherRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private final String uploadPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatars" + File.separator;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
    
    public Optional<User> findById(Long id) {
      return userRepository.findById(id);
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public String saveAvatarAndReturnUrl(Long userId, MultipartFile file, String accountType) throws Exception {
        // 1. Garante que o diretório exista
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 2. Cria nome de arquivo único
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String newFilename = userId + "_" + UUID.randomUUID().toString() + extension;

        // 3. Salva o arquivo no disco
        Path filePath = uploadDir.resolve(newFilename);
        file.transferTo(filePath.toFile());

        // 4. Cria a URL que será salva no DB
        String fileUrl = "/avatars/" + newFilename;

        // 5. Atualiza o usuário (Student ou Teacher) no banco de dados com a nova URL
        findUserAndSetAvatarUrl(userId, fileUrl, accountType); // Chama o método auxiliar

        return fileUrl;
    }

    private User findUserAndSetAvatarUrl(Long userId, String fileUrl, String accountType) {

        // Convertemos o String para o Enum para garantir a comparação correta
        AccountType type = AccountType.valueOf(accountType);

        if (AccountType.STUDENT.equals(type)) {
            Student student = studentRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Estudante não encontrado"));
            student.setAvatarUrl(fileUrl);
            return studentRepository.save(student);

        } else if (AccountType.TEACHER.equals(type)) {
            Teacher teacher = teacherRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));
            teacher.setAvatarUrl(fileUrl);
            return teacherRepository.save(teacher);
        }

        throw new IllegalArgumentException("Tipo de conta inválido para atualização de avatar: " + accountType);
    }
}