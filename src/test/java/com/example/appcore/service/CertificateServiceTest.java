package com.example.appcore.service;

import com.example.appcore.model.Certificate;
import com.example.appcore.model.Course;
import com.example.appcore.model.Student;
import com.example.appcore.repository.CertificateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @InjectMocks
    private CertificateService certificateService;

    // CT01 – Usuário elegível
    @Test
    void deveGerarCertificadoQuandoElegivel() {

        Student aluno = new Student();
        aluno.setId(45L);
        aluno.setName("Jader Vanderlinde");

        Course curso = new Course();
        curso.setId(1L);
        curso.setTitle("Lógica de programação");

        int progresso = 100;

        boolean elegivel = certificateService.verificarElegibilidadeCertificado(progresso, aluno, curso);
        assertTrue(elegivel);

        boolean gerado = certificateService.gerarCertificado(progresso, aluno, curso);
        assertTrue(gerado);

        ArgumentCaptor<Certificate> captor = ArgumentCaptor.forClass(Certificate.class);
        verify(certificateRepository, times(1)).save(captor.capture());

        Certificate saved = captor.getValue();

        assertEquals(aluno, saved.getStudent());
        assertEquals(curso, saved.getCourse());
        assertNotNull(saved.getIssueDate());
    }

    // CT02 – Usuário não elegível
    @Test
    void naoDeveGerarCertificadoQuandoNaoElegivel() {

        Student aluno = new Student();
        aluno.setId(45L);
        aluno.setName("Jader Vanderlinde");

        Course curso = new Course();
        curso.setId(1L);
        curso.setTitle("Lógica de programação");

        int progresso = 80;

        boolean elegivel = certificateService.verificarElegibilidadeCertificado(progresso, aluno, curso);
        assertFalse(elegivel);

        boolean gerado = certificateService.gerarCertificado(progresso, aluno, curso);
        assertFalse(gerado);

        verify(certificateRepository, never()).save(any());
    }
}