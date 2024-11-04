package com.sogonsogon.neighclova.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CheckPasswordRequestDto {

    @NotBlank
    private String password;
}
