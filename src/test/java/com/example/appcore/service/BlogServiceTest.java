package com.example.appcore.service;

import com.example.appcore.model.Blog;
import com.example.appcore.repository.BlogRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlogServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @InjectMocks
    private BlogService blogService;

    public BlogServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    // CT01 — Pesquisa com sucesso
    @Test
    void deveRetornarBlogsRelacionadosQuandoPesquisaForValida() {

        Blog b1 = new Blog();
        b1.setId(1L);
        b1.setTitle("Aprenda Java");

        Blog b2 = new Blog();
        b2.setId(2L);
        b2.setContent("Java é ótimo!");

        when(blogRepository.findByTitleContainingIgnoreCase("Java"))
                .thenReturn(List.of(b1));

        when(blogRepository.findByContentContainingIgnoreCase("Java"))
                .thenReturn(List.of(b2));

        List<Blog> resultado = blogService.searchBlogs("Java");

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(b1));
        assertTrue(resultado.contains(b2));
    }

    // CT02 — Pesquisa sem resultados
    @Test
    void deveRetornarListaVaziaQuandoNenhumBlogForEncontrado() {

        when(blogRepository.findByTitleContainingIgnoreCase("Javozo"))
                .thenReturn(List.of());
        when(blogRepository.findByContentContainingIgnoreCase("Javozo"))
                .thenReturn(List.of());

        List<Blog> resultado = blogService.searchBlogs("Javozo");

        assertTrue(resultado.isEmpty());
    }
  
}
