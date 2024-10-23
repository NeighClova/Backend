package com.sogonsogon.neighclova.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PatchPasswordRequestDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
