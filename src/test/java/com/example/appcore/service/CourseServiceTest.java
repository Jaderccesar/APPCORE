package com.example.appcore.service;

import com.example.appcore.enums.CreateStatus;
import com.example.appcore.model.Comment;
import com.example.appcore.model.Course;
import com.example.appcore.model.Student;
import com.example.appcore.model.User;
import com.example.appcore.repository.CommentRepository;
import com.example.appcore.repository.CourseRepository;
import com.example.appcore.repository.UserRepository;
import com.example.appcore.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


 //* Testes RF09: avaliar e editar avaliações

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setup() {  
    }

    // ---------- RT01: Avaliar curso (casos unitários) ----------

    @Test
    void devePermitirAvaliacaoValida() {
        Course course = new Course();
        course.setId(1L);

        User user = new Student();;
        user.setId(10L);
        user.setName("Daniel");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean resultado = courseService.evaluate(1L, 10L, 5.0, "Excelente curso!");

        assertTrue(resultado, "Avaliação válida deve retornar true");
        assertEquals(1, course.getComments().size(), "Curso deve ter 1 comentário após avaliação");
        Comment savedComment = course.getComments().get(0);

        assertEquals("Excelente curso!", savedComment.getContent());
        assertEquals(5.0, savedComment.getRating());
        assertNotNull(savedComment.getAuthor(), "Autor do comentário não deve ser nulo");
        assertEquals("Daniel", savedComment.getAuthor().getName());

        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void deveFalharComNotaInvalida() {
        // Nota 0 é inválida -> espera false sem salvar
        boolean resultado = courseService.evaluate(1L, 10L, 0.0, "Comentário");
        assertFalse(resultado);
        verify(courseRepository, never()).save(any());
    }

    @Test
    void deveRejeitarComentarioComScript() {

    boolean resultado = courseService.evaluate(1L, 10L, 4.0, "<script>alert('x')</script>");

    assertFalse(resultado);
    verify(courseRepository, never()).save(any()); 
}

    @Test
    void deveFalharComNotaNula() {

      // Nota nula deve retornar false
      boolean resultado = courseService.evaluate(1L, 10L, null, "Bom curso");
      assertFalse(resultado);
      verify(courseRepository, never()).save(any());
    }

    

    // ---------- RT02: Editar avaliação (integra regras de autorização) ----------

    @Test
    void devePermitirEditarComentarioDoProprioUsuario() {
        User user = new Student();;
        user.setId(10L);
        user.setName("Daniel");

        Comment comment = new Comment();
        comment.setId(5L);
        comment.setContent("bom");
        comment.setRating(4.0);
        comment.setAuthor(user);

        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean resultado = courseService.editEvaluate(5L, 10L, 5.0, "Excelente");

        assertTrue(resultado);
        assertEquals(5.0, comment.getRating());
        assertEquals("Excelente", comment.getContent());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void deveNegarEdicaoParaOutroUsuario() {
        User autor = new Student();;
        autor.setId(10L);
        autor.setName("Autor");

        Comment comment = new Comment();
        comment.setId(5L);
        comment.setAuthor(autor);
        comment.setContent("original");
        comment.setRating(4.0);

        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        // usuário 99 não é o autor -> deve lançar RuntimeException (autorizacao)
        assertThrows(RuntimeException.class, () -> courseService.editEvaluate(5L, 99L, 4.0, "tentativa"),
                "Usuário não autor deve lançar RuntimeException");

        verify(commentRepository, never()).save(any());
    }

    @Test
    void deveImpedirCriacaoCursoComNomeInvalido() {
        Course curso = new Course();
        curso.setTitle(" ");
        curso.setDescription("Java para iniciantes");
        curso.setPrice(39.90);
        curso.setWorkload(12);

        assertThrows(IllegalArgumentException.class,
                () -> courseService.save(curso),
                "Nome inválido deve lançar exceção");

        verify(courseRepository, never()).save(any());
    }

    @Test
    void deveImpedirCriacaoCursoComDescricaoInvalida() {
        Course curso = new Course();
        curso.setTitle("Java");
        curso.setDescription(" ");
        curso.setPrice(39.90);
        curso.setWorkload(12);

        assertThrows(IllegalArgumentException.class,
                () -> courseService.save(curso),
                "Descrição inválida deve lançar exceção");

        verify(courseRepository, never()).save(any());
    }

    @Test
    void deveImpedirCriacaoCursoComPrecoInvalido() {
        Course curso = new Course();
        curso.setTitle("Java");
        curso.setDescription("Java para iniciantes");
        curso.setPrice(-40.0);
        curso.setWorkload(12);

        assertThrows(IllegalArgumentException.class,
                () -> courseService.save(curso),
                "Preço inválido deve lançar exceção");

        verify(courseRepository, never()).save(any());
    }

    @Test
    void deveCriarCursoValido() {

        Course curso = new Course();
        curso.setTitle("Java Web");
        curso.setDescription("Curso completo");
        curso.setPrice(59.90);
        curso.setWorkload(20);

        when(courseRepository.save(any(Course.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Course salvo = courseService.save(curso);

        assertNotNull(salvo);
        assertEquals(CreateStatus.DRAFT, salvo.getStatus());
        verify(courseRepository, times(1)).save(curso);
    }

    @Test
    void deveListarSomenteCursosAtivos() {

        Course ativo1 = new Course();
        ativo1.setId(1L);
        ativo1.setTitle("Java");
        ativo1.setStatus(CreateStatus.PUBLISHED);

        Course ativo2 = new Course();
        ativo2.setId(2L);
        ativo2.setTitle("Spring Boot");
        ativo2.setStatus(CreateStatus.PUBLISHED);

        Course inativo = new Course();
        inativo.setId(3L);
        inativo.setTitle("Python");
        inativo.setStatus(CreateStatus.ARCHIVED);

        when(courseRepository.findAll())
                .thenReturn(List.of(ativo1, ativo2, inativo));

        List<Course> resultado = courseService.findAll()
                .stream()
                .filter(c -> c.getStatus() == CreateStatus.PUBLISHED)
                .toList();

        assertEquals(2, resultado.size(), "Deve retornar somente cursos ativos");
        assertTrue(resultado.contains(ativo1));
        assertTrue(resultado.contains(ativo2));
        assertFalse(resultado.contains(inativo));
    }

    @Test
    void deveListarSomenteCursosInativos() {

        Course ativo = new Course();
        ativo.setId(1L);
        ativo.setTitle("Java");
        ativo.setStatus(CreateStatus.PUBLISHED);

        Course inativo1 = new Course();
        inativo1.setId(2L);
        inativo1.setTitle("Python");
        inativo1.setStatus(CreateStatus.ARCHIVED);

        Course inativo2 = new Course();
        inativo2.setId(3L);
        inativo2.setTitle("Banco de Dados");
        inativo2.setStatus(CreateStatus.ARCHIVED);

        when(courseRepository.findAll())
                .thenReturn(List.of(ativo, inativo1, inativo2));

        List<Course> resultado = courseService.findAll()
                .stream()
                .filter(c -> c.getStatus() == CreateStatus.ARCHIVED)
                .toList();

        assertEquals(2, resultado.size(), "Deve retornar somente cursos inativos");
        assertTrue(resultado.contains(inativo1));
        assertTrue(resultado.contains(inativo2));
        assertFalse(resultado.contains(ativo));
    }

}
