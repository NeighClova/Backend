package com.sogonsogon.neighclova.dto.request.introduce;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CreateIntroduceRequestDto {

    @NotBlank
    private List<String> purpose; // 방문 목적

    @NotBlank
    private List<String> service; // 시설 및 서비스

    @NotBlank
    private List<String> mood; // 분위기

    private String emphasizeContent; // 강조 내용
}
