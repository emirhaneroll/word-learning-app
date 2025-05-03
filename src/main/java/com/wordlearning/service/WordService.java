package com.wordlearning.service;

import com.wordlearning.model.Word;
import com.wordlearning.model.WordProgress;
import com.wordlearning.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Collections;

public class WordService {
    private EntityManager entityManager;
    private String lastError;
    
    public WordService() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("wordlearning");
        this.entityManager = emf.createEntityManager();
    }
    
    public String getLastError() {
        return lastError;
    }
    
    public List<Word> getAllWords() {
        try {
            System.out.println("Getting all words from database...");
            entityManager.clear();
            
            TypedQuery<Word> query = entityManager.createQuery(
                "SELECT DISTINCT w FROM Word w LEFT JOIN FETCH w.samples",
                Word.class
            );
            
            List<Word> words = query.getResultList();
            System.out.println("Retrieved " + words.size() + " words from database");
            
            for (Word word : words) {
                System.out.println("Word in database: " + word.getEnglish() + " - " + word.getTurkish() + 
                    " (ID: " + word.getId() + ")" +
                    "\nExample EN: " + word.getExampleSentenceEn() +
                    "\nExample TR: " + word.getExampleSentenceTr());
            }
            
            return words;
        } catch (Exception e) {
            System.err.println("Error in getAllWords: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Word> getWordsForStudy(User user) {
        try {
            System.out.println("Getting words for study for user: " + user.getUsername());
            System.out.println("User's daily word limit: " + user.getNewWordsPerDay());
            entityManager.clear();
            
            // Tüm kelimeleri al
            TypedQuery<Word> allWordsQuery = entityManager.createQuery(
                "SELECT DISTINCT w FROM Word w",
                Word.class
            );
            List<Word> allWords = allWordsQuery.getResultList();
            System.out.println("Total words in database: " + allWords.size());
            
            // Kullanıcının çalıştığı kelimeleri al
            TypedQuery<Word> studiedQuery = entityManager.createQuery(
                "SELECT DISTINCT w FROM Word w JOIN WordProgress wp ON w = wp.word " +
                "WHERE wp.user = :user",
                Word.class
            );
            studiedQuery.setParameter("user", user);
            List<Word> studiedWords = studiedQuery.getResultList();
            System.out.println("Words studied by user: " + studiedWords.size());
            
            // Çalışılmamış kelimeleri bul
            List<Word> newWords = new ArrayList<>(allWords);
            newWords.removeAll(studiedWords);
            System.out.println("New words to study: " + newWords.size());
            
            // Yeni kelimeleri karıştır
            Collections.shuffle(newWords);
            
            // Çalışılmış kelimeleri karıştır
            Collections.shuffle(studiedWords);
            
            // Önce yeni kelimeleri ekle
            List<Word> wordsForStudy = new ArrayList<>();
            int newWordsToAdd = Math.min(newWords.size(), user.getNewWordsPerDay() / 2);
            wordsForStudy.addAll(newWords.subList(0, newWordsToAdd));
            
            // Sonra çalışılmış kelimeleri ekle
            int studiedWordsToAdd = Math.min(studiedWords.size(), user.getNewWordsPerDay() - newWordsToAdd);
            wordsForStudy.addAll(studiedWords.subList(0, studiedWordsToAdd));
            
            // Eğer yeterli kelime yoksa, çalışılmış kelimelerden daha fazla ekle
            if (wordsForStudy.size() < user.getNewWordsPerDay() && !studiedWords.isEmpty()) {
                int remainingWords = user.getNewWordsPerDay() - wordsForStudy.size();
                int additionalWords = Math.min(remainingWords, studiedWords.size());
                wordsForStudy.addAll(studiedWords.subList(0, additionalWords));
            }
            
            // Son kez karıştır
            Collections.shuffle(wordsForStudy);
            
            System.out.println("Total words for study: " + wordsForStudy.size());
            for (Word word : wordsForStudy) {
                System.out.println("Word for study: " + word.getEnglish() + " - " + word.getTurkish());
            }
            
            return wordsForStudy;
        } catch (Exception e) {
            System.err.println("Error in getWordsForStudy: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public void updateWordProgress(Word word, User user, boolean isCorrect) {
        try {
            entityManager.getTransaction().begin();
            
            TypedQuery<WordProgress> query = entityManager.createQuery(
                "SELECT wp FROM WordProgress wp WHERE wp.word = :word AND wp.user = :user", 
                WordProgress.class);
            query.setParameter("word", word);
            query.setParameter("user", user);
            
            WordProgress progress;
            try {
                progress = query.getSingleResult();
            } catch (NoResultException e) {
                progress = new WordProgress();
                progress.setWord(word);
                progress.setUser(user);
                progress.setProficiencyLevel(0);
            }

            int currentLevel = progress.getProficiencyLevel();
            if (isCorrect) {
                progress.setProficiencyLevel(Math.min(currentLevel + 1, 4));
            } else {
                progress.setProficiencyLevel(Math.max(currentLevel - 1, 0));
            }

            progress.setLastReviewDate(LocalDateTime.now());
            progress.setNextReviewDate(calculateNextReviewDate(progress.getProficiencyLevel()));

            if (progress.getId() == null) {
                entityManager.persist(progress);
            } else {
                entityManager.merge(progress);
            }

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }
    
    private LocalDateTime calculateNextReviewDate(int proficiencyLevel) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (proficiencyLevel) {
            case 1:
                return now.plusDays(1);
            case 2:
                return now.plusDays(3);
            case 3:
                return now.plusDays(7);
            case 4:
                return now.plusDays(14);
            default:
                return now.plusHours(1);
        }
    }
    
    public void saveWord(Word word) {
        try {
            System.out.println("Saving word: " + word.getEnglish() + " - " + word.getTurkish());
            entityManager.getTransaction().begin();
            entityManager.persist(word);
            entityManager.getTransaction().commit();
            System.out.println("Word saved successfully");
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Error saving word: " + e.getMessage());
            e.printStackTrace();
            lastError = "Kelime kaydedilirken hata oluştu: " + e.getMessage();
        }
    }
    
    public List<Word> getWordsForReview(User user) {
        try {
            TypedQuery<Word> query = entityManager.createQuery(
                "SELECT w FROM Word w JOIN WordProgress wp ON w = wp.word " +
                "WHERE wp.user = :user AND wp.nextReviewDate <= :now", 
                Word.class);
            query.setParameter("user", user);
            query.setParameter("now", LocalDateTime.now());
            return query.getResultList();
        } catch (Exception e) {
            lastError = "Tekrar edilecek kelimeler alınırken hata oluştu: " + e.getMessage();
            return new ArrayList<>();
        }
    }
    
    public void close() {
        if (entityManager != null) {
            entityManager.close();
        }
    }

    public void addSampleWords() {
        try {
            System.out.println("Adding sample words to database...");
            entityManager.getTransaction().begin();
            
            // Mevcut kelimeleri temizle
            entityManager.createQuery("DELETE FROM Word").executeUpdate();
            
            // Örnek kelimeleri ekle
            List<Word> words = new ArrayList<>();
            
            // Temel kelimeler
            words.add(createWord("hello", "merhaba", 
                "Hello, how are you?", 
                "Merhaba, nasılsın?"));
            words.add(createWord("world", "dünya", 
                "The world is beautiful.", 
                "Dünya güzeldir."));
            words.add(createWord("book", "kitap", 
                "I love reading books.", 
                "Kitap okumayı severim."));
            words.add(createWord("computer", "bilgisayar", 
                "I work on my computer.", 
                "Bilgisayarımda çalışıyorum."));
            words.add(createWord("phone", "telefon", 
                "My phone is new.", 
                "Telefonum yeni."));
            
            // Günlük aktiviteler
            words.add(createWord("eat", "yemek", 
                "I eat breakfast every morning.", 
                "Her sabah kahvaltı yaparım."));
            words.add(createWord("drink", "içmek", 
                "I drink water.", 
                "Su içerim."));
            words.add(createWord("sleep", "uyumak", 
                "I sleep 8 hours every night.", 
                "Her gece 8 saat uyurum."));
            words.add(createWord("walk", "yürümek", 
                "I walk to school.", 
                "Okula yürüyerek giderim."));
            words.add(createWord("run", "koşmak", 
                "I run in the park.", 
                "Parkta koşarım."));
            
            // Duygular
            words.add(createWord("happy", "mutlu", 
                "I am happy today.", 
                "Bugün mutluyum."));
            words.add(createWord("sad", "üzgün", 
                "She looks sad.", 
                "O üzgün görünüyor."));
            words.add(createWord("angry", "kızgın", 
                "Don't be angry.", 
                "Kızma."));
            words.add(createWord("tired", "yorgun", 
                "I am tired after work.", 
                "İşten sonra yorgunum."));
            words.add(createWord("excited", "heyecanlı", 
                "I am excited about the trip.", 
                "Gezi için heyecanlıyım."));
            
            // Hava durumu
            words.add(createWord("sunny", "güneşli", 
                "It's sunny today.", 
                "Bugün hava güneşli."));
            words.add(createWord("rainy", "yağmurlu", 
                "It's rainy outside.", 
                "Dışarısı yağmurlu."));
            words.add(createWord("cloudy", "bulutlu", 
                "The sky is cloudy.", 
                "Gökyüzü bulutlu."));
            words.add(createWord("windy", "rüzgarlı", 
                "It's windy today.", 
                "Bugün rüzgarlı."));
            words.add(createWord("snowy", "karlı", 
                "It's snowy in winter.", 
                "Kışın karlı olur."));
            
            // Yiyecekler
            words.add(createWord("apple", "elma", 
                "I eat an apple every day.", 
                "Her gün bir elma yerim."));
            words.add(createWord("banana", "muz", 
                "Bananas are yellow.", 
                "Muzlar sarıdır."));
            words.add(createWord("orange", "portakal", 
                "Oranges are rich in vitamin C.", 
                "Portakallar C vitamini açısından zengindir."));
            words.add(createWord("bread", "ekmek", 
                "I buy fresh bread every morning.", 
                "Her sabah taze ekmek alırım."));
            words.add(createWord("milk", "süt", 
                "I drink milk before bed.", 
                "Yatmadan önce süt içerim."));
            
            // Meslekler
            words.add(createWord("teacher", "öğretmen", 
                "My mother is a teacher.", 
                "Annem bir öğretmendir."));
            words.add(createWord("doctor", "doktor", 
                "The doctor helped me.", 
                "Doktor bana yardım etti."));
            words.add(createWord("engineer", "mühendis", 
                "He is an engineer.", 
                "O bir mühendistir."));
            words.add(createWord("student", "öğrenci", 
                "I am a student.", 
                "Ben bir öğrenciyim."));
            words.add(createWord("artist", "sanatçı", 
                "She is a famous artist.", 
                "O ünlü bir sanatçıdır."));
            
            // Hayvanlar
            words.add(createWord("dog", "köpek", 
                "I have a dog.", 
                "Bir köpeğim var."));
            words.add(createWord("cat", "kedi", 
                "Cats are independent.", 
                "Kediler bağımsızdır."));
            words.add(createWord("bird", "kuş", 
                "Birds can fly.", 
                "Kuşlar uçabilir."));
            words.add(createWord("fish", "balık", 
                "Fish live in water.", 
                "Balıklar suda yaşar."));
            words.add(createWord("rabbit", "tavşan", 
                "Rabbits like carrots.", 
                "Tavşanlar havuç sever."));
            
            // Ev eşyaları
            words.add(createWord("table", "masa", 
                "The book is on the table.", 
                "Kitap masanın üzerinde."));
            words.add(createWord("chair", "sandalye", 
                "Please sit on the chair.", 
                "Lütfen sandalyeye otur."));
            words.add(createWord("bed", "yatak", 
                "I sleep in my bed.", 
                "Yatağımda uyurum."));
            words.add(createWord("lamp", "lamba", 
                "The lamp is bright.", 
                "Lamba parlak."));
            words.add(createWord("window", "pencere", 
                "Open the window.", 
                "Pencereyi aç."));
            
            // Renkler
            words.add(createWord("red", "kırmızı", 
                "The apple is red.", 
                "Elma kırmızıdır."));
            words.add(createWord("blue", "mavi", 
                "The sky is blue.", 
                "Gökyüzü mavidir."));
            words.add(createWord("green", "yeşil", 
                "Grass is green.", 
                "Çimen yeşildir."));
            words.add(createWord("yellow", "sarı", 
                "The sun is yellow.", 
                "Güneş sarıdır."));
            words.add(createWord("black", "siyah", 
                "The night is black.", 
                "Gece siyahtır."));
            
            // Sayılar
            words.add(createWord("one", "bir", 
                "I have one brother.", 
                "Bir erkek kardeşim var."));
            words.add(createWord("two", "iki", 
                "I have two sisters.", 
                "İki kız kardeşim var."));
            words.add(createWord("three", "üç", 
                "There are three books.", 
                "Üç kitap var."));
            words.add(createWord("four", "dört", 
                "I have four pens.", 
                "Dört kalemim var."));
            words.add(createWord("five", "beş", 
                "I need five minutes.", 
                "Beş dakikaya ihtiyacım var."));
            
            // Zaman
            words.add(createWord("today", "bugün", 
                "Today is Monday.", 
                "Bugün Pazartesi."));
            words.add(createWord("tomorrow", "yarın", 
                "I will go tomorrow.", 
                "Yarın gideceğim."));
            words.add(createWord("yesterday", "dün", 
                "I was at home yesterday.", 
                "Dün evdeydim."));
            words.add(createWord("morning", "sabah", 
                "Good morning!", 
                "Günaydın!"));
            words.add(createWord("night", "gece", 
                "Good night!", 
                "İyi geceler!"));
            
            // Haftanın günleri
            words.add(createWord("Monday", "Pazartesi", 
                "I start work on Monday.", 
                "Pazartesi işe başlarım."));
            words.add(createWord("Tuesday", "Salı", 
                "Tuesday is a busy day.", 
                "Salı yoğun bir gün."));
            words.add(createWord("Wednesday", "Çarşamba", 
                "Wednesday is the middle of the week.", 
                "Çarşamba haftanın ortasıdır."));
            words.add(createWord("Thursday", "Perşembe", 
                "Thursday is almost Friday.", 
                "Perşembe neredeyse Cuma."));
            words.add(createWord("Friday", "Cuma", 
                "Friday is the last workday.", 
                "Cuma son iş günü."));
            
            // Aylar
            words.add(createWord("January", "Ocak", 
                "January is the first month.", 
                "Ocak ilk aydır."));
            words.add(createWord("February", "Şubat", 
                "February is short.", 
                "Şubat kısadır."));
            words.add(createWord("March", "Mart", 
                "March is spring.", 
                "Mart bahardır."));
            words.add(createWord("April", "Nisan", 
                "April showers bring May flowers.", 
                "Nisan yağmurları Mayıs çiçeklerini getirir."));
            words.add(createWord("May", "Mayıs", 
                "May is beautiful.", 
                "Mayıs güzeldir."));
            
            // Mevsimler
            words.add(createWord("spring", "ilkbahar", 
                "Spring is my favorite season.", 
                "İlkbahar en sevdiğim mevsim."));
            words.add(createWord("summer", "yaz", 
                "Summer is hot.", 
                "Yaz sıcaktır."));
            words.add(createWord("autumn", "sonbahar", 
                "Autumn is colorful.", 
                "Sonbahar renklidir."));
            words.add(createWord("winter", "kış", 
                "Winter is cold.", 
                "Kış soğuktur."));
            words.add(createWord("season", "mevsim", 
                "There are four seasons.", 
                "Dört mevsim vardır."));
            
            // Doğa
            words.add(createWord("mountain", "dağ", 
                "The mountain is high.", 
                "Dağ yüksektir."));
            words.add(createWord("river", "nehir", 
                "The river is long.", 
                "Nehir uzundur."));
            words.add(createWord("ocean", "okyanus", 
                "The ocean is deep.", 
                "Okyanus derindir."));
            words.add(createWord("forest", "orman", 
                "The forest is green.", 
                "Orman yeşildir."));
            words.add(createWord("beach", "plaj", 
                "The beach is beautiful.", 
                "Plaj güzeldir."));
            
            // Şehir
            words.add(createWord("city", "şehir", 
                "I live in a big city.", 
                "Büyük bir şehirde yaşıyorum."));
            words.add(createWord("street", "sokak", 
                "The street is busy.", 
                "Sokak kalabalık."));
            words.add(createWord("building", "bina", 
                "The building is tall.", 
                "Bina yüksek."));
            words.add(createWord("park", "park", 
                "The park is peaceful.", 
                "Park huzurlu."));
            words.add(createWord("bridge", "köprü", 
                "The bridge is old.", 
                "Köprü eski."));
            
            // Ulaşım
            words.add(createWord("car", "araba", 
                "I drive a car.", 
                "Araba kullanırım."));
            words.add(createWord("bus", "otobüs", 
                "I take the bus.", 
                "Otobüse binerim."));
            words.add(createWord("train", "tren", 
                "The train is fast.", 
                "Tren hızlıdır."));
            words.add(createWord("plane", "uçak", 
                "The plane is in the sky.", 
                "Uçak gökyüzünde."));
            words.add(createWord("bicycle", "bisiklet", 
                "I ride a bicycle.", 
                "Bisiklet sürerim."));
            
            // Aile
            words.add(createWord("mother", "anne", 
                "My mother is kind.", 
                "Annem naziktir."));
            words.add(createWord("father", "baba", 
                "My father is strong.", 
                "Babam güçlüdür."));
            words.add(createWord("sister", "kız kardeş", 
                "I have a sister.", 
                "Bir kız kardeşim var."));
            words.add(createWord("brother", "erkek kardeş", 
                "I have a brother.", 
                "Bir erkek kardeşim var."));
            words.add(createWord("family", "aile", 
                "Family is important.", 
                "Aile önemlidir."));
            
            // Vücut
            words.add(createWord("head", "baş", 
                "My head hurts.", 
                "Başım ağrıyor."));
            words.add(createWord("hand", "el", 
                "I write with my hand.", 
                "Elimle yazarım."));
            words.add(createWord("foot", "ayak", 
                "I walk with my feet.", 
                "Ayaklarımla yürürüm."));
            words.add(createWord("eye", "göz", 
                "I see with my eyes.", 
                "Gözlerimle görürüm."));
            words.add(createWord("mouth", "ağız", 
                "I speak with my mouth.", 
                "Ağzımla konuşurum."));
            
            // Giysiler
            words.add(createWord("shirt", "gömlek", 
                "I wear a shirt.", 
                "Gömlek giyerim."));
            words.add(createWord("pants", "pantolon", 
                "I wear pants.", 
                "Pantolon giyerim."));
            words.add(createWord("shoes", "ayakkabı", 
                "I wear shoes.", 
                "Ayakkabı giyerim."));
            words.add(createWord("hat", "şapka", 
                "I wear a hat.", 
                "Şapka takarım."));
            words.add(createWord("coat", "mont", 
                "I wear a coat.", 
                "Mont giyerim."));
            
            // Kelimeleri veritabanına kaydet
            for (Word word : words) {
                entityManager.persist(word);
            }
            
            entityManager.getTransaction().commit();
            System.out.println("Added " + words.size() + " sample words");
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Error adding sample words: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Word createWord(String english, String turkish, String exampleEn, String exampleTr) {
        Word word = new Word();
        word.setEnglish(english);
        word.setTurkish(turkish);
        word.setExampleSentenceEn(exampleEn);
        word.setExampleSentenceTr(exampleTr);
        return word;
    }
} 