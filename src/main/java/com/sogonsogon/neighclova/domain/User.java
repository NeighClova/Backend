package com.sogonsogon.neighclova.domain;

import com.sogonsogon.neighclova.dto.request.auth.SignUpRequestDto;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String uid;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean status;

    @CreatedDate
    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    @Column(length = 20)
    private String type;

    public User(SignUpRequestDto dto) {
        this.uid = dto.getUid();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.status = true;
        this.created_at = LocalDateTime.now();
        this.type = "app";
    }

    public void patchPassword(String password) {
        this.password = password;
        this.updated_at = LocalDateTime.now();
    }

    public void patchStatus() {
        this.status = false;
    }

    public User(String email, String type) {
        this.uid = email + "n";
        this.email = email;
        this.password = "password";
        this.status = true;
        this.created_at = LocalDateTime.now();
        this.type = type;
    }
}
