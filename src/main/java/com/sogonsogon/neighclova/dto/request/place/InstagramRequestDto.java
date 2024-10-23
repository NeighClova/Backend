package com.sogonsogon.neighclova.dto.request.place;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InstagramRequestDto {
    @NotBlank
    private Long placeId;

    @NotBlank
    private String id;

    @NotBlank
    private String password;
}
