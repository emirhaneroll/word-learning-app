package com.wordlearning.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "word_samples")
public class WordSample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sample", nullable = false)
    private String sample;

    @Column(name = "sentence", nullable = true)
    private String sentence;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }
} 