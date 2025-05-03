package com.wordlearning;

import com.wordlearning.service.WordService;
import java.util.List;
import com.wordlearning.model.Word;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class AddSampleWords {
    public static void main(String[] args) {
        try {
            System.out.println("Starting database verification...");
            
            // Test database connection
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("wordlearning");
            EntityManager em = emf.createEntityManager();
            System.out.println("Database connection successful");
            
            // Create WordService
            WordService wordService = new WordService();
            
            // First check if there are any words
            System.out.println("\nChecking existing words...");
            List<Word> existingWords = wordService.getAllWords();
            System.out.println("Found " + existingWords.size() + " existing words");
            
            // Add sample words
            System.out.println("\nAdding sample words...");
            wordService.addSampleWords();
            
            // Verify words were added
            System.out.println("\nVerifying words were added...");
            List<Word> wordsAfter = wordService.getAllWords();
            System.out.println("Total words in database: " + wordsAfter.size());
            
            // Print all words
            System.out.println("\nAll words in database:");
            for (Word word : wordsAfter) {
                System.out.println(word.getEnglish() + " - " + word.getTurkish());
            }
            
            // Clean up
            wordService.close();
            em.close();
            emf.close();
            
            System.out.println("\nDatabase verification completed");
        } catch (Exception e) {
            System.err.println("Error during database verification: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 