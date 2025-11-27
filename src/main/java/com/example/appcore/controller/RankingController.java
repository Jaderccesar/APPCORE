package com.example.appcore.controller;

import com.example.appcore.model.Student;
import com.example.appcore.repository.StudentRepository;
import com.example.appcore.service.RankingRealService;
import com.example.appcore.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    @Autowired
    private RankingRealService rankingRealService;

    @GetMapping
    public List<Map<String, Object>> getRanking() {
        return rankingRealService.getRealRanking();
    }
}




