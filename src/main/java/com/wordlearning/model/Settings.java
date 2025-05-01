package com.wordlearning.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private int dailyGoal;
    private int reviewInterval;
    private boolean notificationsEnabled;
    
    public Settings() {
        this.dailyGoal = 10;
        this.reviewInterval = 7;
        this.notificationsEnabled = true;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public int getDailyGoal() {
        return dailyGoal;
    }
    
    public void setDailyGoal(int dailyGoal) {
        this.dailyGoal = dailyGoal;
    }
    
    public int getReviewInterval() {
        return reviewInterval;
    }
    
    public void setReviewInterval(int reviewInterval) {
        this.reviewInterval = reviewInterval;
    }
    
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }
    
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
} 