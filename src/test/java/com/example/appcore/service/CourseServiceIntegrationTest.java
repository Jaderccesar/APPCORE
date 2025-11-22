package com.example.appcore.service;

import com.example.appcore.enums.CreateStatus;
import com.example.appcore.enums.Difficulty;
import com.example.appcore.model.Challenge;
import com.example.appcore.model.Comment;
import com.example.appcore.model.Course;
import com.example.appcore.model.Student;
import com.example.appcore.repository.ChallengeRepository;
import com.example.appcore.repository.CommentRepository;
import com.example.appcore.repository.CourseRepository;
import com.example.appcore.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class CourseServiceIntegrationTest{

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void deveListarSomenteCursosAtivos_integration() {

        Course ativo1 = new Course();
        ativo1.setTitle("Java");
        ativo1.setStatus(CreateStatus.PUBLISHED);
        ativo1.setCertificateEnabled(false);
        ativo1.setWorkload(10);
        ativo1.setPrice(0.0);
        ativo1.setRating(0.0);

        Course ativo2 = new Course();
        ativo2.setTitle("Spring Boot");
        ativo2.setStatus(CreateStatus.PUBLISHED);
        ativo2.setCertificateEnabled(false);
        ativo2.setWorkload(10);
        ativo2.setPrice(0.0);
        ativo2.setRating(0.0);

        Course inativo = new Course();
        inativo.setTitle("Python");
        inativo.setStatus(CreateStatus.ARCHIVED);
        inativo.setCertificateEnabled(false);
        inativo.setWorkload(10);
        inativo.setPrice(0.0);
        inativo.setRating(0.0);

        courseRepository.saveAll(List.of(ativo1, ativo2, inativo));

        List<Course> todos = courseService.findAll();
        List<Course> resultado = todos.stream()
                .filter(c -> c.getStatus() == CreateStatus.PUBLISHED)
                .toList();

        assertEquals(2, resultado.size());
    }


    @Test
    void deveListarSomenteCursosInativos_integration() {

        Course ativo = new Course();
        ativo.setTitle("Java");
        ativo.setDescription("Curso de Java");
        ativo.setImageUrl("img1.jpg");
        ativo.setPrice(100.0);
        ativo.setRating(4.5);
        ativo.setWorkload(20);
        ativo.setLevel("Intermediário");
        ativo.setCertificateEnabled(true);
        ativo.setStatus(CreateStatus.PUBLISHED);

        Course inativo1 = new Course();
        inativo1.setTitle("Python");
        inativo1.setDescription("Curso de Python");
        inativo1.setImageUrl("img2.jpg");
        inativo1.setPrice(120.0);
        inativo1.setRating(4.7);
        inativo1.setWorkload(18);
        inativo1.setLevel("Básico");
        inativo1.setCertificateEnabled(true);
        inativo1.setStatus(CreateStatus.ARCHIVED);

        Course inativo2 = new Course();
        inativo2.setTitle("Banco de Dados");
        inativo2.setDescription("Curso de SQL e NoSQL");
        inativo2.setImageUrl("img3.jpg");
        inativo2.setPrice(150.0);
        inativo2.setRating(4.8);
        inativo2.setWorkload(25);
        inativo2.setLevel("Avançado");
        inativo2.setCertificateEnabled(true);
        inativo2.setStatus(CreateStatus.ARCHIVED);

        courseRepository.saveAll(List.of(ativo, inativo1, inativo2));

        List<Course> todos = courseService.findAll();

        List<Course> resultado = todos.stream()
                .filter(c -> c.getStatus() == CreateStatus.ARCHIVED)
                .toList();

        assertEquals(2, resultado.size(), "Deve retornar somente cursos inativos");
    }

   @Test
    void devePermitirEditarComentarioDoProprioUsuario() {
        
        Student user = new Student();
        user.setName("Daniel");
        user = userRepository.save(user); 

        // Criar comentário real
        Comment comment = new Comment();
        comment.setContent("bom");
        comment.setRating(4.0);
        comment.setAuthor(user);
        comment = commentRepository.save(comment); 

        
        boolean result = courseService.editEvaluate(
                comment.getId(),
                user.getId(),
                5.0,
                "Excelente"
        );

        assertTrue(result);

        Comment atualizado = commentRepository.findById(comment.getId()).orElseThrow();

        assertEquals(5.0, atualizado.getRating());
        assertEquals("Excelente", atualizado.getContent());
    }

    @Test
    void deveNegarEdicaoParaOutroUsuario() {
       
        Student autor = new Student();
        autor.setName("Autor");
        autor = userRepository.save(autor);

        
        Student outrotemp = new Student();
        outrotemp.setName("Intruso");
        Student outro = userRepository.save(outrotemp);

        
        Comment commentTemp = new Comment();
        commentTemp.setContent("original");
        commentTemp.setRating(4.0);
        commentTemp.setAuthor(autor);

        Comment comment = commentRepository.save(commentTemp);

        
        assertThrows(RuntimeException.class, () ->
            courseService.editEvaluate(
                    comment.getId(),
                    outro.getId(),
                    4.0,
                    "tentativa"
            )
        );

        // garante que NÃO alterou nada
        Comment naoAlterado = commentRepository.findById(comment.getId()).orElseThrow();
        assertEquals("original", naoAlterado.getContent());
        assertEquals(4.0, naoAlterado.getRating());
    }

}
