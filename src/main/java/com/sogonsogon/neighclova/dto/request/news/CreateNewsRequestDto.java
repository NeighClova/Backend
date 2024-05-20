package com.sogonsogon.neighclova.dto.request.news;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateNewsRequestDto {

    @NotBlank
    private String keyword; // 가게 소식 키워드

    @NotBlank
    private String newsType; // 소식 유형

    private String newsDetail; // 추가 소식 유형

    private String startDate; // 시작 기간

    private String endDate; // 종료 기간

    private String highlightContent; // 강조하고 싶은 내용
}
