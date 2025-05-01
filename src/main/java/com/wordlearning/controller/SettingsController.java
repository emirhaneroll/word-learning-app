package com.wordlearning.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.wordlearning.service.SettingsService;

public class SettingsController {
    @FXML
    private TextField dailyGoalField;
    
    @FXML
    private TextField reviewIntervalField;
    
    @FXML
    private CheckBox notificationsCheckBox;
    
    private SettingsService settingsService = new SettingsService();
    
    @FXML
    public void initialize() {
        loadSettings();
    }
    
    private void loadSettings() {
        dailyGoalField.setText(String.valueOf(settingsService.getDailyGoal()));
        reviewIntervalField.setText(String.valueOf(settingsService.getReviewInterval()));
        notificationsCheckBox.setSelected(settingsService.isNotificationsEnabled());
    }
    
    @FXML
    private void handleSave() {
        try {
            int dailyGoal = Integer.parseInt(dailyGoalField.getText());
            int reviewInterval = Integer.parseInt(reviewIntervalField.getText());
            boolean notifications = notificationsCheckBox.isSelected();
            
            if (dailyGoal < 1 || reviewInterval < 1) {
                showAlert("Hata", "Değerler 1'den büyük olmalıdır.", AlertType.ERROR);
                return;
            }
            
            settingsService.saveSettings(dailyGoal, reviewInterval, notifications);
            showAlert("Başarılı", "Ayarlar kaydedildi.", AlertType.INFORMATION);
            handleBack();
        } catch (NumberFormatException e) {
            showAlert("Hata", "Lütfen geçerli sayısal değerler girin.", AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dailyGoalField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
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