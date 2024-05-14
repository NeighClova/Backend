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
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long placeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(length=30, nullable = false)
    private String placeName;

    private Long placeNum;

    @Column(length=50, nullable = false)
    private String category;

    @Column(nullable = false)
    private String placeUrl;

    private String profileImg;

    private String targetAge;

    private String target;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
