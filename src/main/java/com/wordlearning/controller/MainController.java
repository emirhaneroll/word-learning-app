package com.wordlearning.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.wordlearning.model.User;
import com.wordlearning.service.WordService;
import com.wordlearning.model.Word;
import java.util.List;

public class MainController {
    @FXML
    private StackPane contentArea;
    
    private User currentUser;
    private WordService wordService;
    
    @FXML
    public void initialize() {
        try {
            System.out.println("MainController initialize başladı");
            wordService = new WordService();
            
            // Kelime sayısını kontrol et
            List<Word> allWords = wordService.getAllWords();
            System.out.println("Mevcut kelime sayısı: " + allWords.size());
            
            if (allWords.size() < 100) {
                System.out.println("Örnek kelimeler ekleniyor...");
                wordService.addSampleWords();
                allWords = wordService.getAllWords();
                System.out.println("Yeni kelime sayısı: " + allWords.size());
            }
            
            // Varsayılan olarak kelime çalışma ekranını yükle
            try {
                loadContent("/fxml/study-words.fxml");
            } catch (Exception e) {
                System.err.println("Study words screen yüklenirken hata: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("MainController initialize hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    @FXML
    private void handleStudyWords() {
        loadContent("/fxml/study-words.fxml");
    }
    
    @FXML
    private void handleAddWord() {
        loadContent("/fxml/add-word.fxml");
    }
    
    @FXML
    private void handleAddSampleWords() {
        try {
            WordService wordService = new WordService();
            wordService.addSampleWords();
            showAlert("Bilgi", "Örnek kelimeler başarıyla eklendi.", AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Hata", "Örnek kelimeler eklenirken bir hata oluştu: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleStatistics() {
        loadContent("/fxml/statistics.fxml");
    }
    
    @FXML
    private void handleSettings() {
        loadContent("/fxml/settings.fxml");
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showAlert("Hata", "Çıkış yapılırken bir hata oluştu.", AlertType.ERROR);
        }
    }
    
    private void loadContent(String fxmlPath) {
        try {
            System.out.println("Loading content: " + fxmlPath);
            System.out.println("Current user: " + (currentUser != null ? currentUser.getUsername() : "null"));
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            
            // Controller'a kullanıcı bilgisini aktar
            if (fxmlPath.equals("/fxml/study-words.fxml")) {
                StudyWordsController controller = loader.getController();
                controller.setCurrentUser(currentUser);
                System.out.println("User set for StudyWordsController");
            } else if (fxmlPath.equals("/fxml/add-word.fxml")) {
                AddWordController controller = loader.getController();
                controller.setCurrentUser(currentUser);
                System.out.println("User set for AddWordController");
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
            System.out.println("Content loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading content: " + e.getMessage());
            e.printStackTrace();
            // Sadece gerçek hata durumlarında alert göster
            if (!fxmlPath.equals("/fxml/dashboard.fxml")) {
                showAlert("Hata", "İçerik yüklenirken bir hata oluştu.", AlertType.ERROR);
            }
        }
    }
    
    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 