package com.wordlearning.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.wordlearning.service.UserService;
import com.wordlearning.model.User;
import java.util.prefs.Preferences;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private CheckBox rememberMeCheckbox;
    
    private UserService userService = new UserService();
    private static final String PREF_USERNAME = "saved_username";
    private static final String PREF_PASSWORD = "saved_password";
    private static final String PREF_REMEMBER_ME = "remember_me";
    
    @FXML
    public void initialize() {
        // Load saved credentials if they exist
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        boolean rememberMe = prefs.getBoolean(PREF_REMEMBER_ME, false);
        rememberMeCheckbox.setSelected(rememberMe);
        
        if (rememberMe) {
            String savedUsername = prefs.get(PREF_USERNAME, "");
            String savedPassword = prefs.get(PREF_PASSWORD, "");
            usernameField.setText(savedUsername);
            passwordField.setText(savedPassword);
        }
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Hata", "Lütfen tüm alanları doldurun.", AlertType.ERROR);
            return;
        }
        
        User user = userService.login(username, password);
        if (user != null) {
            // Save credentials if "Remember Me" is checked
            Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
            if (rememberMeCheckbox.isSelected()) {
                prefs.put(PREF_USERNAME, username);
                prefs.put(PREF_PASSWORD, password);
                prefs.putBoolean(PREF_REMEMBER_ME, true);
            } else {
                prefs.remove(PREF_USERNAME);
                prefs.remove(PREF_PASSWORD);
                prefs.putBoolean(PREF_REMEMBER_ME, false);
            }
            
            loadMainScreen(user);
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
    
    private void loadMainScreen(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            // MainController'a kullanıcı bilgisini aktar
            MainController mainController = loader.getController();
            mainController.setCurrentUser(user);
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            System.err.println("Ana ekran yüklenirken hata: " + e.getMessage());
            e.printStackTrace();
            showAlert("Hata", "Ana ekran yüklenirken bir hata oluştu. Lütfen tekrar deneyin.", AlertType.ERROR);
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