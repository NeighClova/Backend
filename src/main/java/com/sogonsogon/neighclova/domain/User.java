package com.sogonsogon.neighclova.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(length=50, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean status;

    @CreatedDate
    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    @Column(length=20)
    private String type;
}
