package com.wordlearning.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MainController {
    @FXML
    private StackPane contentArea;
    
    @FXML
    private void handleStudyWords() {
        loadContent("/fxml/study-words.fxml");
    }
    
    @FXML
    private void handleAddWord() {
        loadContent("/fxml/add-word.fxml");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (Exception e) {
            showAlert("Hata", "İçerik yüklenirken bir hata oluştu.", AlertType.ERROR);
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