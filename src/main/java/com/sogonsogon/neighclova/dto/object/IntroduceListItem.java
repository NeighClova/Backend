package com.sogonsogon.neighclova.dto.object;

import com.sogonsogon.neighclova.domain.Introduce;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IntroduceListItem {
    private Long introduceId;
    private String content;
    private LocalDateTime createdAt;

    public static IntroduceListItem of(Introduce introduce) {
        return IntroduceListItem.builder()
                .introduceId(introduce.getIntroduceId())
                .content(introduce.getContent())
                .createdAt(introduce.getCreatedAt())
                .build();
    }
}
