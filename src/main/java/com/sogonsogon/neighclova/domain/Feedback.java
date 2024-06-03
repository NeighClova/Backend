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
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place placeId;

    private String pSummary;

    @Column(columnDefinition = "TEXT")
    private String pBody;

    private String nSummary;

    @Column(columnDefinition = "TEXT")
    private String nBody;

    @Column(columnDefinition = "TEXT")
    private String keyword;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(length=10, nullable = false)
    private String viewDate;
}
