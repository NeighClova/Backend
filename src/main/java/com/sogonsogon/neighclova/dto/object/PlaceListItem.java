package com.sogonsogon.neighclova.dto.object;

import com.sogonsogon.neighclova.domain.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceListItem {
    private Long placeId;
    private String placeName;
    private String profileImg;

    public static PlaceListItem of(Place place) {
        return PlaceListItem.builder()
                .placeId(place.getPlaceId())
                .placeName(place.getPlaceName())
                .profileImg(place.getProfileImg())
                .build();
    }
}
