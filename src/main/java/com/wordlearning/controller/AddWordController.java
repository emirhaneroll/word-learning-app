package com.wordlearning.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.wordlearning.model.Word;
import com.wordlearning.model.WordSample;
import com.wordlearning.model.User;
import com.wordlearning.service.WordService;
import java.io.File;
import java.util.HashSet;

public class AddWordController {
    @FXML
    private TextField englishWordField;
    
    @FXML
    private TextField turkishWordField;
    
    @FXML
    private TextArea sampleField;
    
    @FXML
    private TextField imageUrlField;
    
    @FXML
    private TextField audioUrlField;
    
    private WordService wordService;
    private User currentUser;
    
    public AddWordController() {
        wordService = new WordService();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    @FXML
    private void handleSave() {
        if (currentUser == null) {
            showAlert("Hata", "Lütfen önce giriş yapın.", AlertType.ERROR);
            return;
        }
        
        String englishWord = englishWordField.getText().trim();
        String turkishWord = turkishWordField.getText().trim();
        String sample = sampleField.getText().trim();
        String imageUrl = imageUrlField.getText().trim();
        String audioUrl = audioUrlField.getText().trim();
        
        if (englishWord.isEmpty() || turkishWord.isEmpty()) {
            showAlert("Hata", "İngilizce kelime ve Türkçe karşılığı zorunludur.", AlertType.ERROR);
            return;
        }
        
        try {
            Word word = new Word();
            word.setEnglish(englishWord);
            word.setTurkish(turkishWord);
            word.setImageUrl(imageUrl);
            word.setAudioUrl(audioUrl);
            
            if (!sample.isEmpty()) {
                WordSample wordSample = new WordSample();
                wordSample.setWord(word);
                wordSample.setSample(sample);
                word.setSamples(new HashSet<>());
                word.getSamples().add(wordSample);
            }
            
            wordService.saveWord(word);
            showAlert("Başarılı", "Kelime başarıyla kaydedildi.", AlertType.INFORMATION);
            handleClear();
        } catch (Exception e) {
            showAlert("Hata", "Kelime kaydedilirken bir hata oluştu: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleClear() {
        englishWordField.clear();
        turkishWordField.clear();
        sampleField.clear();
        imageUrlField.clear();
        audioUrlField.clear();
    }
    
    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Görsel Seç");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Görsel Dosyaları", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File file = fileChooser.showOpenDialog(imageUrlField.getScene().getWindow());
        if (file != null) {
            imageUrlField.setText(file.getAbsolutePath());
        }
    }
    
    @FXML
    private void handleSelectAudio() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ses Dosyası Seç");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Ses Dosyaları", "*.mp3", "*.wav")
        );
        
        File file = fileChooser.showOpenDialog(audioUrlField.getScene().getWindow());
        if (file != null) {
            audioUrlField.setText(file.getAbsolutePath());
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