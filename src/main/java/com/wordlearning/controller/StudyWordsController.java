package com.wordlearning.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.wordlearning.model.Word;
import com.wordlearning.model.WordProgress;
import com.wordlearning.service.WordService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class StudyWordsController {
    @FXML
    private Spinner<Integer> wordCountSpinner;
    
    @FXML
    private StackPane wordCardArea;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Label progressLabel;
    
    private WordService wordService;
    private List<Word> currentWords;
    private int currentIndex = 0;
    private boolean showingAnswer = false;
    
    public StudyWordsController() {
        wordService = new WordService();
    }
    
    @FXML
    private void handleStartStudy() {
        int wordCount = wordCountSpinner.getValue();
        currentWords = wordService.getWordsForStudy(wordCount);
        
        if (currentWords.isEmpty()) {
            showAlert("Bilgi", "Çalışılacak kelime bulunamadı.", AlertType.INFORMATION);
            return;
        }
        
        currentIndex = 0;
        updateProgress();
        showCurrentWord();
    }
    
    @FXML
    private void handleKnow() {
        if (currentWords == null || currentWords.isEmpty()) return;
        
        Word currentWord = currentWords.get(currentIndex);
        wordService.updateWordProgress(currentWord, true);
        
        moveToNextWord();
    }
    
    @FXML
    private void handleDontKnow() {
        if (currentWords == null || currentWords.isEmpty()) return;
        
        Word currentWord = currentWords.get(currentIndex);
        wordService.updateWordProgress(currentWord, false);
        
        moveToNextWord();
    }
    
    private void moveToNextWord() {
        currentIndex++;
        if (currentIndex >= currentWords.size()) {
            showCompletionMessage();
        } else {
            updateProgress();
            showCurrentWord();
        }
    }
    
    private void showCurrentWord() {
        Word currentWord = currentWords.get(currentIndex);
        
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
        
        Text wordText = new Text(currentWord.getEnglishWord());
        wordText.setStyle("-fx-font-size: 24px;");
        
        Button showAnswerButton = new Button("Cevabı Göster");
        showAnswerButton.setOnAction(e -> showAnswer(currentWord, card));
        
        card.getChildren().addAll(wordText, showAnswerButton);
        wordCardArea.getChildren().clear();
        wordCardArea.getChildren().add(card);
        
        showingAnswer = false;
    }
    
    private void showAnswer(Word word, VBox card) {
        if (showingAnswer) return;
        
        Text translation = new Text(word.getTurkishWord());
        translation.setStyle("-fx-font-size: 20px; -fx-fill: #666;");
        
        if (word.getSamples() != null && !word.getSamples().isEmpty()) {
            Text sample = new Text(word.getSamples().get(0).getSample());
            sample.setStyle("-fx-font-size: 16px; -fx-fill: #888;");
            card.getChildren().addAll(translation, sample);
        } else {
            card.getChildren().add(translation);
        }
        
        showingAnswer = true;
    }
    
    private void updateProgress() {
        double progress = (double) currentIndex / currentWords.size();
        progressBar.setProgress(progress);
        progressLabel.setText(String.format("%d/%d kelime tamamlandı", currentIndex, currentWords.size()));
    }
    
    private void showCompletionMessage() {
        VBox message = new VBox(10);
        message.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
        
        Text completionText = new Text("Tebrikler! Bugünkü kelimeleri tamamladınız.");
        completionText.setStyle("-fx-font-size: 20px;");
        
        Button restartButton = new Button("Yeniden Başla");
        restartButton.setOnAction(e -> handleStartStudy());
        
        message.getChildren().addAll(completionText, restartButton);
        wordCardArea.getChildren().clear();
        wordCardArea.getChildren().add(message);
    }
    
    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 