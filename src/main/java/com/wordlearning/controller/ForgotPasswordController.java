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

public class ForgotPasswordController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    private UserService userService = new UserService();
    
    @FXML
    private void handleResetPassword() {
        String username = usernameField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Hata", "Lütfen tüm alanları doldurun.", AlertType.ERROR);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showAlert("Hata", "Şifreler eşleşmiyor.", AlertType.ERROR);
            return;
        }
        
        if (userService.resetPassword(username, newPassword)) {
            showAlert("Başarılı", "Şifreniz başarıyla sıfırlandı.", AlertType.INFORMATION);
            handleBack();
        } else {
            showAlert("Hata", userService.getLastError(), AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showAlert("Hata", "Giriş ekranına dönülürken bir hata oluştu: " + e.getMessage(), AlertType.ERROR);
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