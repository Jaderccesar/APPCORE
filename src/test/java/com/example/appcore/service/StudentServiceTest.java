package com.example.appcore.service;

import com.example.appcore.model.Student;
import com.example.appcore.repository.AddressRepository;
import com.example.appcore.repository.StudentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private StudentService studentService;

    private Student mockStudent;

    @BeforeEach
    void setup() {
        mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setEmail("Leandro123@gmail.com");
        mockStudent.setPassword("123Leandro");
        mockStudent.setName("Leandro");
    }

    @Test
    void devePermitirLoginComCredenciaisValidas() {

        when(studentRepository.findByEmail("Leandro123@gmail.com"))
                .thenReturn(Optional.of(mockStudent));

        boolean resultado = studentService.autenticarStudent(
                "Leandro123@gmail.com",
                "123Leandro"
        );

        assertTrue(resultado, "Login válido deve retornar true");
        verify(studentRepository, times(1)).findByEmail("Leandro123@gmail.com");
    }

    @Test
    void deveRejeitarLoginComEmailInvalido() {

        boolean resultado = studentService.autenticarStudent(
                "Leandro123#gmail,com",
                "123Leandro"
        );

        assertFalse(resultado, "E-mail inválido deve impedir autenticação");
        verify(studentRepository, never()).findByEmail(anyString());
    }

    @Test
    void deveRejeitarLoginComSenhaIncorreta() {

        when(studentRepository.findByEmail("Leandro123@gmail.com"))
                .thenReturn(Optional.of(mockStudent));

        boolean resultado = studentService.autenticarStudent(
                "Leandro123@gmail.com",
                "SenhaErrada"
        );

        assertFalse(resultado, "Senha incorreta deve impedir login");
        verify(studentRepository, times(1)).findByEmail("Leandro123@gmail.com");
    }
}
