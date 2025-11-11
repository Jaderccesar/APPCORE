package com.example.appcore.appcore.service;

import com.example.appcore.appcore.model.Mission;
import com.example.appcore.appcore.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    public List<Mission> findAll() {
        return missionRepository.findAll();
    }

    public Mission findById(Long id) {
        return missionRepository.findById(id).get();
    }

    public Mission save(Mission mission) {
        return missionRepository.save(mission);
    }

    public Mission update(Long id, Mission mission) {
        Mission existing = missionRepository.findById(id).orElseThrow(() -> new RuntimeException("Missão com o id " + id + " não encontrada"));

        existing.setTitle(mission.getTitle());
        existing.setDescription(mission.getDescription());
        existing.setType(mission.getType());
        existing.setObjective(mission.getObjective());
        existing.setPoints(mission.getPoints());
        existing.setStartDate(mission.getStartDate());
        existing.setEndDate(mission.getEndDate());
        existing.setStatus(mission.getStatus());

        return missionRepository.save(existing);
    }

    public void delete(Long id) {
        missionRepository.deleteById(id);
    }
}
