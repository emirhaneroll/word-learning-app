package com.wordlearning.controller;

import com.wordlearning.model.Word;
import com.wordlearning.model.User;
import com.wordlearning.model.WordSample;
import com.wordlearning.service.WordService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.*;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.scene.Node;
import javafx.animation.RotateTransition;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.stream.Collectors;

public class StudyWordsController {
    @FXML private StackPane wordCard;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;
    @FXML private Spinner<Integer> wordCountSpinner;

    private WordService wordService;
    private List<Word> words;
    private int currentIndex;
    private User currentUser;
    private Map<Long, Boolean> userAnswers = new HashMap<>();

    @FXML
    public void initialize() {
        wordService = new WordService();
        System.out.println("StudyWordsController initialized");
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("Current user set: " + (user != null ? user.getUsername() : "null"));
    }

    @FXML
    public void handleStartStudy() {
        System.out.println("handleStartStudy called");
        System.out.println("Current user: " + (currentUser != null ? currentUser.getUsername() : "null"));
        System.out.println("Word count spinner value: " + wordCountSpinner.getValue());
        
        if (currentUser == null) {
            showAlert("Hata", "Lütfen önce giriş yapın.");
            return;
        }

        try {
            words = wordService.getWordsForStudy(currentUser);
            System.out.println("Retrieved words: " + (words != null ? words.size() : 0));
            
            if (words == null || words.isEmpty()) {
                showAlert("Bilgi", "Çalışılacak kelime bulunamadı. Lütfen önce kelime ekleyin.");
                return;
            }

            currentIndex = 0;
            showCurrentWord();
            updateProgress();
            
            System.out.println("Study session started successfully");
        } catch (Exception e) {
            System.err.println("Error in handleStartStudy: " + e.getMessage());
            e.printStackTrace();
            showAlert("Hata", "Kelimeler yüklenirken bir hata oluştu.");
        }
    }

    private void showCurrentWord() {
        System.out.println("showCurrentWord called, currentIndex: " + currentIndex);
        System.out.println("Total words: " + (words != null ? words.size() : 0));
        
        if (currentIndex >= words.size()) {
            showCompletionMessage();
            return;
        }

        try {
            Word currentWord = words.get(currentIndex);
            System.out.println("Showing word: " + currentWord.getEnglish() + " - " + currentWord.getTurkish());
            
            List<String> options = generateOptions(currentWord);
            System.out.println("Generated options: " + options);
            
            wordCard.getChildren().clear();
            
            VBox cardContent = new VBox(20);
            cardContent.setAlignment(Pos.CENTER);
            cardContent.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
            cardContent.prefWidthProperty().bind(wordCard.widthProperty().multiply(0.8));
            cardContent.prefHeightProperty().bind(wordCard.heightProperty().multiply(0.8));
            cardContent.setMaxWidth(800);
            cardContent.setMaxHeight(600);
            
            // Kelime ve butonlar için HBox
            HBox wordBox = new HBox(10);
            wordBox.setAlignment(Pos.CENTER);
            
            Label wordLabel = new Label(currentWord.getEnglish());
            wordLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
            wordLabel.setWrapText(true);
            
            // Ses butonu
            Button soundButton = new Button("🔊");
            soundButton.setStyle("-fx-font-size: 24px; -fx-background-color: transparent; -fx-cursor: hand;");
            soundButton.setOnAction(e -> playWordSound(currentWord.getEnglish()));
            
            // Örnek cümle butonu
            Button exampleButton = new Button("💬");
            exampleButton.setStyle("-fx-font-size: 24px; -fx-background-color: transparent; -fx-cursor: hand;");
            exampleButton.setOnAction(e -> showExampleCard(cardContent, currentWord));
            
            // Resim butonu
            Button imageButton = new Button("🖼️");
            imageButton.setStyle("-fx-font-size: 24px; -fx-background-color: transparent; -fx-cursor: hand;");
            imageButton.setOnAction(e -> showWordImage(currentWord.getEnglish()));
            
            wordBox.getChildren().addAll(wordLabel, soundButton, exampleButton, imageButton);
            cardContent.getChildren().add(wordBox);
            
            VBox optionsBox = new VBox(10);
            optionsBox.setMaxWidth(400);
            optionsBox.prefWidthProperty().bind(cardContent.widthProperty().multiply(0.8));
            
            if (userAnswers.containsKey(currentWord.getId())) {
                boolean isCorrect = userAnswers.get(currentWord.getId());
                for (String option : options) {
                    Button optionButton = new Button(option);
                    optionButton.setMaxWidth(Double.MAX_VALUE);
                    optionButton.setStyle("-fx-font-size: 16px; -fx-padding: 10;");
                    optionButton.setDisable(true);
                    optionButton.setWrapText(true);
                    
                    if (option.equals(currentWord.getTurkish())) {
                        optionButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    } else if (!isCorrect) {
                        optionButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                    }
                    optionsBox.getChildren().add(optionButton);
                }
                
                showNavigationButtons(cardContent);
            } else {
                for (String option : options) {
                    Button optionButton = new Button(option);
                    optionButton.setMaxWidth(Double.MAX_VALUE);
                    optionButton.setStyle("-fx-font-size: 16px; -fx-padding: 10;");
                    optionButton.setOnAction(e -> handleAnswer(option, currentWord));
                    optionButton.setWrapText(true);
                    optionsBox.getChildren().add(optionButton);
                }
            }
            
            cardContent.getChildren().add(optionsBox);
            wordCard.getChildren().add(cardContent);
            System.out.println("Word card created and added to scene");
        } catch (Exception e) {
            System.err.println("Error in showCurrentWord: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<String> generateOptions(Word correctWord) {
        List<String> options = new ArrayList<>();
        options.add(correctWord.getTurkish());
        
        try {
            List<Word> allWords = wordService.getAllWords();
            allWords.removeIf(word -> word.getId().equals(correctWord.getId()));
            Collections.shuffle(allWords);
            
            for (int i = 0; i < Math.min(3, allWords.size()); i++) {
                options.add(allWords.get(i).getTurkish());
            }
            
            Collections.shuffle(options);
            return options;
        } catch (Exception e) {
            System.err.println("Error generating options: " + e.getMessage());
            e.printStackTrace();
            // Hata durumunda sadece doğru cevabı döndür
            return Collections.singletonList(correctWord.getTurkish());
        }
    }

    private void playWordSound(String word) {
        try {
            // Text-to-speech için JavaFX'in MediaPlayer'ını kullan
            String url = "https://ssl.gstatic.com/dictionary/static/sounds/oxford/" + word.toLowerCase() + "--_gb_1.mp3";
            Media media = new Media(url);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    private void showWordImage(String word) {
        try {
            // Google Images API'yi kullanarak kelime ile ilgili resim göster
            String url = "https://source.unsplash.com/featured/?" + word.toLowerCase();
            Image image = new Image(url);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(300);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
            
            VBox imageBox = new VBox(10);
            imageBox.setAlignment(Pos.CENTER);
            imageBox.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-background-radius: 10px;");
            imageBox.getChildren().add(imageView);
            
            // Resmi göster
            wordCard.getChildren().add(imageBox);
        } catch (Exception e) {
            System.err.println("Error showing image: " + e.getMessage());
        }
    }

    private void showExampleCard(VBox cardContent, Word currentWord) {
        VBox exampleCard = new VBox(20);
        exampleCard.setAlignment(Pos.CENTER);
        exampleCard.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
        exampleCard.prefWidthProperty().bind(cardContent.widthProperty().multiply(0.9));
        
        Label exampleTitle = new Label("Örnek Cümle");
        exampleTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        String exampleEn = currentWord.getExampleSentenceEn();
        if (exampleEn == null || exampleEn.trim().isEmpty()) {
            exampleEn = "This is an example sentence using the word '" + currentWord.getEnglish() + "'.";
        }
        Label exampleText = new Label(exampleEn);
        exampleText.setStyle("-fx-font-size: 18px;");
        exampleText.setWrapText(true);
        exampleText.prefWidthProperty().bind(exampleCard.widthProperty().multiply(0.9));
        
        String exampleTr = currentWord.getExampleSentenceTr();
        if (exampleTr == null || exampleTr.trim().isEmpty()) {
            exampleTr = "Bu, '" + currentWord.getTurkish() + "' kelimesini kullanan örnek bir cümledir.";
        }
        Label turkishExample = new Label(exampleTr);
        turkishExample.setStyle("-fx-font-size: 18px; -fx-font-style: italic;");
        turkishExample.setWrapText(true);
        turkishExample.prefWidthProperty().bind(exampleCard.widthProperty().multiply(0.9));
        
        exampleCard.getChildren().addAll(exampleTitle, exampleText, turkishExample);
        
        // Örnek cümle kartını göster
        cardContent.getChildren().add(exampleCard);
    }

    private void showNavigationButtons(VBox cardContent) {
        HBox navigationBox = new HBox(20);
        navigationBox.setAlignment(Pos.CENTER);
        navigationBox.prefWidthProperty().bind(cardContent.widthProperty().multiply(0.9));
        
        Button prevButton = new Button("← Önceki Kelime");
        prevButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        prevButton.setDisable(currentIndex == 0);
        prevButton.setOnAction(e -> {
            currentIndex--;
            showCurrentWord();
            updateProgress();
        });
        
        Button nextButton = new Button("Sonraki Kelime →");
        nextButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        nextButton.setDisable(currentIndex == words.size() - 1);
        nextButton.setOnAction(e -> {
            currentIndex++;
            showCurrentWord();
            updateProgress();
        });
        
        navigationBox.getChildren().addAll(prevButton, nextButton);
        cardContent.getChildren().add(navigationBox);
    }

    private void handleAnswer(String selectedAnswer, Word currentWord) {
        boolean isCorrect = selectedAnswer.equals(currentWord.getTurkish());
        userAnswers.put(currentWord.getId(), isCorrect);
        
        VBox cardContent = (VBox) wordCard.getChildren().get(0);
        VBox optionsBox = (VBox) cardContent.getChildren().get(1);
        
        for (Node node : optionsBox.getChildren()) {
            Button button = (Button) node;
            button.setDisable(true);
            
            if (button.getText().equals(selectedAnswer)) {
                button.setStyle(isCorrect ? 
                    "-fx-background-color: #4CAF50; -fx-text-fill: white;" : 
                    "-fx-background-color: #f44336; -fx-text-fill: white;");
            } else if (button.getText().equals(currentWord.getTurkish())) {
                button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }
        }
        
        showNavigationButtons(cardContent);
        wordService.updateWordProgress(currentWord, currentUser, isCorrect);
        
        // Update progress after answering
        updateProgress();
        
        // Check if all words have been answered
        if (userAnswers.size() == words.size()) {
            showCompletionMessage();
        }
    }

    private void updateProgress() {
        int total = words.size();
        int completed = currentIndex;
        double progress = (double) completed / total;
        
        progressBar.setProgress(progress);
        progressLabel.setText(String.format("%d / %d", completed, total));
    }

    private void showCompletionMessage() {
        wordCard.getChildren().clear();
        
        VBox completionBox = new VBox(20);
        completionBox.setAlignment(Pos.CENTER);
        completionBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
        
        Label completionLabel = new Label("Tebrikler! Tüm kelimeleri tamamladınız.");
        completionLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Button restartButton = new Button("Tekrar Başla");
        restartButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        restartButton.setOnAction(e -> handleStartStudy());
        
        completionBox.getChildren().addAll(completionLabel, restartButton);
        wordCard.getChildren().add(completionBox);
        
        progressBar.setProgress(1.0);
        progressLabel.setText(String.format("%d / %d", words.size(), words.size()));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 