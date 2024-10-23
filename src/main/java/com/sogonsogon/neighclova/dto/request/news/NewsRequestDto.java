package com.sogonsogon.neighclova.dto.request.news;

import com.sogonsogon.neighclova.domain.News;
import com.sogonsogon.neighclova.domain.Place;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NewsRequestDto {

    @NotBlank
    private Long placeId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String keyword;

    public News toEntity(Place place) {
        return News.builder()
                .placeId(place)
                .keyword(keyword)
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
