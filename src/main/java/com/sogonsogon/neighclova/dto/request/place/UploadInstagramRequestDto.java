package com.sogonsogon.neighclova.dto.request.place;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UploadInstagramRequestDto {

    @NotBlank
    private Long placeId;

    @NotBlank
    private String content;
}
