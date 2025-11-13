package com.example.appcore.service;

import com.example.appcore.model.Promotion;
import com.example.appcore.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    public List<Promotion> getPromotions() {
        return promotionRepository.findAll();
    }

    public Optional<Promotion> getPromotion(Long id) {
        return promotionRepository.findById(id);
    }

    public Promotion save(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    public Promotion update(Long id, Promotion promotion) {
        Promotion existing = promotionRepository.findById(id).orElseThrow(() -> new RuntimeException("Promoção com id " + id + " não encontrada"));

        existing.setTitle(promotion.getTitle());
        existing.setDescription(promotion.getDescription());
        existing.setDiscountPercentage(promotion.getDiscountPercentage());
        existing.setDiscountAmount(promotion.getDiscountAmount());
        existing.setStartDate(promotion.getStartDate());
        existing.setEndDate(promotion.getEndDate());

        return promotionRepository.save(promotion);
    }

    public void delete(Long id) {
        promotionRepository.deleteById(id);
    }
}
