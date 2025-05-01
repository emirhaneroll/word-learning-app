package com.wordlearning.service;

import com.wordlearning.model.Settings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class SettingsService {
    private EntityManagerFactory emf;
    private EntityManager em;
    
    public SettingsService() {
        emf = Persistence.createEntityManagerFactory("wordlearning");
        em = emf.createEntityManager();
    }
    
    public int getDailyGoal() {
        Settings settings = getSettings();
        return settings != null ? settings.getDailyGoal() : 10;
    }
    
    public int getReviewInterval() {
        Settings settings = getSettings();
        return settings != null ? settings.getReviewInterval() : 7;
    }
    
    public boolean isNotificationsEnabled() {
        Settings settings = getSettings();
        return settings != null ? settings.isNotificationsEnabled() : true;
    }
    
    private Settings getSettings() {
        TypedQuery<Settings> query = em.createQuery(
            "SELECT s FROM Settings s", 
            Settings.class
        );
        List<Settings> settings = query.getResultList();
        return settings.isEmpty() ? null : settings.get(0);
    }
    
    public void saveSettings(int dailyGoal, int reviewInterval, boolean notificationsEnabled) {
        try {
            em.getTransaction().begin();
            
            Settings settings = getSettings();
            if (settings == null) {
                settings = new Settings();
            }
            
            settings.setDailyGoal(dailyGoal);
            settings.setReviewInterval(reviewInterval);
            settings.setNotificationsEnabled(notificationsEnabled);
            
            em.persist(settings);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
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