package com.wordlearning.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "english_word", nullable = false)
    private String englishWord;

    @Column(name = "turkish_word", nullable = false)
    private String turkishWord;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "audio_url")
    private String audioUrl;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<WordSample> samples;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<WordProgress> wordProgresses;
} 