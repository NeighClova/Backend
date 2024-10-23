package com.sogonsogon.neighclova.dto.request.place;

import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class PlaceRequestDto {
    private String placeName;
    private String category;
    private String placeUrl;
    private String profileImg;
    private String placeNum;
    private List<String> targetAge;
    private List<String> target;

    public Place toEntity(User user) {
        String targetString = String.join(",", target);
        String ageString = String.join(",", targetAge);

        return Place.builder()
                .userId(user)
                .placeName(placeName)
                .category(category)
                .placeUrl(placeUrl)
                .profileImg(profileImg)
                .placeNum(placeNum)
                .targetAge(ageString)
                .target(targetString)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
