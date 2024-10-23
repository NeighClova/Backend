package com.sogonsogon.neighclova.domain;

import com.sogonsogon.neighclova.dto.request.place.InstagramRequestDto;
import com.sogonsogon.neighclova.dto.request.place.PlaceRequestDto;
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

    @Column(length=30)
    private String placeNum;

    @Column(length=50, nullable = false)
    private String category;

    @Column(nullable = false)
    private String placeUrl;

    private String profileImg;

    private String targetAge;

    private String target;

    @Column(length=50)
    private String instagramId;

    private String instagramPw;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public void patchPlace(PlaceRequestDto dto, User user) {
        String targetString = String.join(",", dto.getTarget());
        String ageString = String.join(",", dto.getTargetAge());

        this.userId = user;
        this.placeName = dto.getPlaceName();
        this.category = dto.getCategory();
        this.placeUrl = dto.getPlaceUrl();
        this.target = targetString;
        this.targetAge = ageString;
        this.updatedAt = LocalDateTime.now();
    }

    public void patchProfileImg(String url) {
        this.profileImg = url;
    }

    public void patchInstagram(InstagramRequestDto dto) {
        this.instagramId = dto.getId();
        this.instagramPw = dto.getPassword();
    }
}
