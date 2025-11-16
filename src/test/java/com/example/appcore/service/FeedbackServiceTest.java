package com.example.appcore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.appcore.model.Comment;
import com.example.appcore.model.Course;
import com.example.appcore.model.Student;
import com.example.appcore.model.Teacher;
import com.example.appcore.model.User;
import com.example.appcore.repository.CommentRepository;
import com.example.appcore.repository.CourseRepository;
import com.example.appcore.repository.TeacherRepository;
import com.example.appcore.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;


    // CT01 - Responder feedback com sucesso 
    @Test
    void deveValidarRespostaComSucesso() {

        String reply = "Vamos revisar o módulo.";

        boolean result = commentService.validateReply(reply);

        assertTrue(result);
    }

    // CT02 - Sem feedback disponível 
    @Test
    void deveRetornarListaVaziaQuandoNaoExistemFeedbacks() {

          Long courseId = 1L;
          Long teacherId = 10L;

          Teacher teacher = new Teacher();
          teacher.setId(teacherId);
          teacher.setName("Daniel");

          when(userRepository.findById(teacherId))
                  .thenReturn(Optional.of(teacher));

          Course course = new Course();
          course.setId(courseId);

          when(courseRepository.findById(courseId))
                  .thenReturn(Optional.of(course));

          when(commentRepository.findByCourseId(courseId))
                  .thenReturn(List.of());

          List<Comment> feedbacks = commentService.manageFeedbacks(courseId, teacherId);

          assertNotNull(feedbacks);
          assertTrue(feedbacks.isEmpty());
    }

    // CT03 - Resposta vazia 
    @Test
    void deveFalharQuandoRespostaForVazia() {

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.validateReply("")
        );

        assertEquals("Campo de resposta obrigatório", exception.getMessage()); 
    }
}
