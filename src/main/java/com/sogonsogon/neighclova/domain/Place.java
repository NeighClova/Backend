package com.sogonsogon.neighclova.domain;

import com.sogonsogon.neighclova.dto.request.PlaceRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

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

    public void patchPlace(PlaceRequestDto dto, User user) {
        String targetString = String.join(",", target);
        String ageString = String.join(",", targetAge);

        this.userId = user;
        this.placeName = dto.getPlaceName();
        this.category = dto.getCategory();
        this.placeUrl = dto.getPlaceUrl();
        this.profileImg = dto.getProfileImg();
        this.target = targetString;
        this.targetAge = ageString;
        this.updatedAt = LocalDateTime.now();
    }
}
