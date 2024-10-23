package com.sogonsogon.neighclova.dto.request.introduce;

import com.sogonsogon.neighclova.domain.Introduce;
import com.sogonsogon.neighclova.domain.Place;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class IntroduceRequestDto {

    @NotBlank
    private Long placeId;

    @NotBlank
    private String content;

    public Introduce toEntity(Place place) {
        return Introduce.builder()
                .placeId(place)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
