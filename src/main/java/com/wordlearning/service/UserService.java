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
            System.out.println("UserService initialized successfully");
        } catch (Exception e) {
            lastError = "Veritabanı bağlantısı kurulamadı: " + e.getMessage();
            System.err.println("Error initializing UserService: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public String getLastError() {
        return lastError;
    }
    
    public User login(String username, String password) {
        try {
            System.out.println("Attempting login for user: " + username);
            
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username AND u.password = :password", 
                User.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);
            
            List<User> users = query.getResultList();
            User user = users.isEmpty() ? null : users.get(0);
            
            if (user != null) {
                System.out.println("Login successful for user: " + username);
            } else {
                System.out.println("Login failed for user: " + username);
                lastError = "Kullanıcı adı veya şifre hatalı.";
            }
            
            return user;
        } catch (Exception e) {
            lastError = "Giriş yapılırken hata oluştu: " + e.getMessage();
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean register(String username, String password) {
        try {
            System.out.println("Attempting registration for user: " + username);
            
            // Kullanıcı adı kontrolü
            TypedQuery<User> checkQuery = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", 
                User.class
            );
            checkQuery.setParameter("username", username);
            
            if (!checkQuery.getResultList().isEmpty()) {
                lastError = "Bu kullanıcı adı zaten kullanılıyor.";
                System.out.println("Registration failed: Username already exists");
                return false;
            }
            
            em.getTransaction().begin();
            
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setNewWordsPerDay(10); // Varsayılan değer
            
            em.persist(user);
            em.getTransaction().commit();
            
            System.out.println("Registration successful for user: " + username);
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            lastError = "Kayıt olurken hata oluştu: " + e.getMessage();
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean resetPassword(String username, String newPassword) {
        try {
            System.out.println("Attempting password reset for user: " + username);
            
            em.getTransaction().begin();
            
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", 
                User.class
            );
            query.setParameter("username", username);
            
            List<User> users = query.getResultList();
            if (users.isEmpty()) {
                lastError = "Kullanıcı bulunamadı.";
                System.out.println("Password reset failed: User not found");
                return false;
            }
            
            User user = users.get(0);
            user.setPassword(newPassword);
            
            em.getTransaction().commit();
            
            System.out.println("Password reset successful for user: " + username);
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            lastError = "Şifre sıfırlanırken hata oluştu: " + e.getMessage();
            System.err.println("Error during password reset: " + e.getMessage());
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