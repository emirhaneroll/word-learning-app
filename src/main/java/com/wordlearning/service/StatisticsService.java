package com.wordlearning.service;

import com.wordlearning.model.Word;
import com.wordlearning.model.WordProgress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class StatisticsService {
    private EntityManagerFactory emf;
    private EntityManager em;
    private String lastError;
    
    public StatisticsService() {
        try {
            emf = Persistence.createEntityManagerFactory("wordlearning");
            em = emf.createEntityManager();
        } catch (Exception e) {
            lastError = "Veritabanı bağlantısı kurulamadı: " + e.getMessage();
            e.printStackTrace();
        }
    }
    
    public String getLastError() {
        return lastError;
    }
    
    public long getTotalWords() {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(w) FROM Word w", 
                Long.class
            );
            return query.getSingleResult();
        } catch (Exception e) {
            lastError = "Toplam kelime sayısı alınırken hata oluştu: " + e.getMessage();
            e.printStackTrace();
            return 0;
        }
    }
    
    public long getLearnedWords() {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(wp) FROM WordProgress wp WHERE wp.proficiencyLevel >= 4", 
                Long.class
            );
            return query.getSingleResult();
        } catch (Exception e) {
            lastError = "Öğrenilen kelime sayısı alınırken hata oluştu: " + e.getMessage();
            e.printStackTrace();
            return 0;
        }
    }
    
    public long getStudiedWords() {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(DISTINCT wp.word) FROM WordProgress wp", 
                Long.class
            );
            return query.getSingleResult();
        } catch (Exception e) {
            lastError = "Çalışılan kelime sayısı alınırken hata oluştu: " + e.getMessage();
            e.printStackTrace();
            return 0;
        }
    }
    
    public double getSuccessRate() {
        try {
            // Önce çalışılan kelime sayısını kontrol et
            long studiedWords = getStudiedWords();
            if (studiedWords == 0) {
                return 0.0;
            }
            
            // Tüm kelimelerin ortalama başarı seviyesini hesapla
            TypedQuery<Double> query = em.createQuery(
                "SELECT AVG(wp.proficiencyLevel) FROM WordProgress wp", 
                Double.class
            );
            Double result = query.getSingleResult();
            
            // Eğer sonuç null ise veya geçersizse 0 döndür
            if (result == null || result.isNaN() || result.isInfinite()) {
                return 0.0;
            }
            
            // Başarı oranını yüzde olarak hesapla (5 üzerinden)
            return (result / 5.0) * 100.0;
        } catch (Exception e) {
            lastError = "Başarı oranı hesaplanırken hata oluştu: " + e.getMessage();
            e.printStackTrace();
            return 0.0;
        }
    }
    
    public void close() {
        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }
} 