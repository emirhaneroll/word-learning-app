package com.wordlearning.service;

import com.wordlearning.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class UserService {
    private EntityManagerFactory emf;
    private EntityManager em;
    private String lastError;
    
    public UserService() {
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
    
    public boolean login(String username, String password) {
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username AND u.password = :password", 
                User.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);
            
            List<User> users = query.getResultList();
            return !users.isEmpty();
        } catch (Exception e) {
            lastError = "Giriş yapılırken hata oluştu: " + e.getMessage();
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean register(String username, String password) {
        try {
            // Kullanıcı adı kontrolü
            TypedQuery<User> checkQuery = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", 
                User.class
            );
            checkQuery.setParameter("username", username);
            
            if (!checkQuery.getResultList().isEmpty()) {
                lastError = "Bu kullanıcı adı zaten kullanılıyor.";
                return false;
            }
            
            em.getTransaction().begin();
            
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            
            em.persist(user);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            lastError = "Kayıt olurken hata oluştu: " + e.getMessage();
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean resetPassword(String username, String newPassword) {
        try {
            em.getTransaction().begin();
            
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", 
                User.class
            );
            query.setParameter("username", username);
            
            List<User> users = query.getResultList();
            if (users.isEmpty()) {
                lastError = "Kullanıcı bulunamadı.";
                return false;
            }
            
            User user = users.get(0);
            user.setPassword(newPassword);
            
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            lastError = "Şifre sıfırlanırken hata oluştu: " + e.getMessage();
            e.printStackTrace();
            return false;
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