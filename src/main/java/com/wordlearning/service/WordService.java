package com.wordlearning.service;

import com.wordlearning.model.Word;
import com.wordlearning.model.WordProgress;
import com.wordlearning.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class WordService {
    private EntityManagerFactory emf;
    private EntityManager em;
    
    public WordService() {
        emf = Persistence.createEntityManagerFactory("wordlearning");
        em = emf.createEntityManager();
    }
    
    public void saveWord(Word word) {
        em.getTransaction().begin();
        try {
            em.persist(word);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    public List<Word> getWordsForStudy(int count) {
        // TODO: Implement proper user session management
        User currentUser = getCurrentUser();
        
        TypedQuery<Word> query = em.createQuery(
            "SELECT w FROM Word w " +
            "LEFT JOIN WordProgress wp ON wp.word = w AND wp.user = :user " +
            "WHERE wp.id IS NULL OR " +
            "(wp.nextReviewDate <= :now AND wp.isLearned = false) " +
            "ORDER BY wp.nextReviewDate ASC NULLS FIRST", 
            Word.class
        );
        
        query.setParameter("user", currentUser);
        query.setParameter("now", LocalDateTime.now());
        query.setMaxResults(count);
        
        return query.getResultList();
    }
    
    public void updateWordProgress(Word word, boolean isCorrect) {
        // TODO: Implement proper user session management
        User currentUser = getCurrentUser();
        
        em.getTransaction().begin();
        
        TypedQuery<WordProgress> query = em.createQuery(
            "SELECT wp FROM WordProgress wp " +
            "WHERE wp.word = :word AND wp.user = :user", 
            WordProgress.class
        );
        
        query.setParameter("word", word);
        query.setParameter("user", currentUser);
        
        List<WordProgress> progresses = query.getResultList();
        WordProgress progress;
        
        if (progresses.isEmpty()) {
            progress = new WordProgress();
            progress.setWord(word);
            progress.setUser(currentUser);
            progress.setCorrectCount(0);
            progress.setLastReviewDate(LocalDateTime.now());
        } else {
            progress = progresses.get(0);
        }
        
        if (isCorrect) {
            progress.setCorrectCount(progress.getCorrectCount() + 1);
            if (progress.getCorrectCount() >= 6) {
                progress.setIsLearned(true);
            } else {
                progress.setNextReviewDate(calculateNextReviewDate(progress.getCorrectCount()));
            }
        } else {
            progress.setCorrectCount(0);
            progress.setNextReviewDate(LocalDateTime.now().plusDays(1));
        }
        
        progress.setLastReviewDate(LocalDateTime.now());
        
        if (progresses.isEmpty()) {
            em.persist(progress);
        }
        
        em.getTransaction().commit();
    }
    
    private LocalDateTime calculateNextReviewDate(int correctCount) {
        LocalDateTime now = LocalDateTime.now();
        switch (correctCount) {
            case 1: return now.plusDays(1);
            case 2: return now.plusWeeks(1);
            case 3: return now.plusMonths(1);
            case 4: return now.plusMonths(3);
            case 5: return now.plusMonths(6);
            default: return now.plusYears(1);
        }
    }
    
    private User getCurrentUser() {
        // TODO: Implement proper user session management
        // For now, return a dummy user
        User user = new User();
        user.setId(1L);
        return user;
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