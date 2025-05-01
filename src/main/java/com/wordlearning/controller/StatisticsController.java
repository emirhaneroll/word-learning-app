package com.wordlearning.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.wordlearning.service.StatisticsService;

public class StatisticsController {
    @FXML
    private Label totalWordsLabel;
    
    @FXML
    private Label learnedWordsLabel;
    
    @FXML
    private Label studiedWordsLabel;
    
    @FXML
    private Label successRateLabel;
    
    private StatisticsService statisticsService = new StatisticsService();
    
    @FXML
    public void initialize() {
        updateStatistics();
    }
    
    private void updateStatistics() {
        try {
            // Toplam kelime sayısı
            long totalWords = statisticsService.getTotalWords();
            totalWordsLabel.setText(String.valueOf(totalWords));
            
            // Öğrenilen kelime sayısı
            long learnedWords = statisticsService.getLearnedWords();
            learnedWordsLabel.setText(String.valueOf(learnedWords));
            
            // Çalışılan kelime sayısı
            long studiedWords = statisticsService.getStudiedWords();
            studiedWordsLabel.setText(String.valueOf(studiedWords));
            
            // Başarı oranı
            double successRate = statisticsService.getSuccessRate();
            if (studiedWords > 0) {
                successRateLabel.setText(String.format("%.1f%%", successRate));
            } else {
                successRateLabel.setText("Henüz çalışma yapılmadı");
            }
            
            // Hata kontrolü
            String error = statisticsService.getLastError();
            if (error != null) {
                showAlert("Uyarı", error, AlertType.WARNING);
            }
        } catch (Exception e) {
            showAlert("Hata", "İstatistikler yüklenirken bir hata oluştu: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) totalWordsLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showAlert("Hata", "Ana ekrana dönülürken bir hata oluştu: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
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