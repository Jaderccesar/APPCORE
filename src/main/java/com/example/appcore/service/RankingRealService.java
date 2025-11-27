package com.example.appcore.service;

import com.example.appcore.model.Student;
import com.example.appcore.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RankingRealService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Map<String, Object>> getRealRanking() {

        List<Student> students = studentRepository.findAll();

        List<Map<String, Object>> ranking = new ArrayList<>();

        for (Student s : students) {

            Map<String, Object> item = new HashMap<>();

            item.put("user_name", s.getName());
            item.put("total_score", s.getTotalScore());
            item.put("challenges_completed", s.getNivel());
            item.put("id", s.getId());

            ranking.add(item);
        }

        ranking.sort((a, b) -> ((Integer)b.get("total_score")) - ((Integer)a.get("total_score")));

        return ranking;
    }
}
