package com.wordlearning.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Data
@Entity
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "english", nullable = false)
    private String english;

    @Column(name = "turkish", nullable = false)
    private String turkish;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "example_sentence_en")
    private String exampleSentenceEn;

    @Column(name = "example_sentence_tr")
    private String exampleSentenceTr;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<WordSample> samples = new HashSet<>();

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<WordProgress> progress = new HashSet<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getTurkish() {
        return turkish;
    }

    public void setTurkish(String turkish) {
        this.turkish = turkish;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getExampleSentenceEn() {
        return exampleSentenceEn;
    }

    public void setExampleSentenceEn(String exampleSentenceEn) {
        this.exampleSentenceEn = exampleSentenceEn;
    }

    public String getExampleSentenceTr() {
        return exampleSentenceTr;
    }

    public void setExampleSentenceTr(String exampleSentenceTr) {
        this.exampleSentenceTr = exampleSentenceTr;
    }

    public Set<WordSample> getSamples() {
        return samples;
    }

    public void setSamples(Set<WordSample> samples) {
        this.samples = samples;
    }

    public Set<WordProgress> getProgress() {
        return progress;
    }

    public void setProgress(Set<WordProgress> progress) {
        this.progress = progress;
    }
} 