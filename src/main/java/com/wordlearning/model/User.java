package com.wordlearning.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "new_words_per_day", nullable = false)
    private Integer newWordsPerDay = 10;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WordProgress> wordProgresses;
} 