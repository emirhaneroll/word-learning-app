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

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(nullable = false)
    private String sample;
} 