package com.example.appcore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.appcore.model.Telephone;
import com.example.appcore.repository.TelephoneRepository;

@Service
public class TelephoneService {

  private final TelephoneRepository telephoneRepository;

    public TelephoneService(TelephoneRepository repository) {
        this.telephoneRepository = repository; 
    }

    public List<Telephone> findAll() {
        return telephoneRepository.findAll();
    }

    public Optional<Telephone> findById(Long id) {
        return telephoneRepository.findById(id);
    }

    public Telephone create(Telephone telephone) {
        return telephoneRepository.save(telephone);
    }

    public Telephone update(Long id, Telephone updated) {
        return telephoneRepository.findById(id)
                .map(existing -> {
                    existing.setNumber(updated.getNumber());
                    existing.setPhoneType(updated.getPhoneType());
                    existing.setUser(updated.getUser());
                    return telephoneRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Telephone não encontrada: " + id));
    }

    public void delete(Long id) {
        telephoneRepository.deleteById(id);
    }

    public List<Telephone> findByUserId(Long userId) {
        return telephoneRepository.findByUserId(userId);
    }
}
