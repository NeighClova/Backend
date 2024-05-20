package com.sogonsogon.neighclova.dto.object;

import com.sogonsogon.neighclova.domain.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewsListItem {
    private Long newsId;
    private Long placeId;
    private String placeName;
    private String profileImg;
    private LocalDateTime createdAt;
    private String title;
    private String content;

    public static NewsListItem of(News news) {
        return NewsListItem.builder()
                .newsId(news.getNewsId())
                .placeId(news.getPlaceId().getPlaceId())
                .placeName(news.getPlaceId().getPlaceName())
                .profileImg(news.getPlaceId().getProfileImg())
                .createdAt(news.getCreatedAt())
                .title(news.getTitle())
                .content(news.getContent())
                .build();
    }
}
