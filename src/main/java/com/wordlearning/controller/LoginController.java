package com.wordlearning.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.wordlearning.service.UserService;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    private UserService userService = new UserService();
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Hata", "Lütfen tüm alanları doldurun.", AlertType.ERROR);
            return;
        }
        
        if (userService.login(username, password)) {
            loadMainScreen();
        } else {
            showAlert("Hata", userService.getLastError(), AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Hata", "Lütfen tüm alanları doldurun.", AlertType.ERROR);
            return;
        }
        
        if (userService.register(username, password)) {
            showAlert("Başarılı", "Kayıt işlemi başarıyla tamamlandı. Şimdi giriş yapabilirsiniz.", AlertType.INFORMATION);
        } else {
            showAlert("Hata", userService.getLastError(), AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/forgot-password.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showAlert("Hata", "Şifre sıfırlama ekranı yüklenirken bir hata oluştu: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    private void loadMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showAlert("Hata", "Ana ekran yüklenirken bir hata oluştu: " + e.getMessage(), AlertType.ERROR);
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