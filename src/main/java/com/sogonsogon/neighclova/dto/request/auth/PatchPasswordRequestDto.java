package com.sogonsogon.neighclova.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PatchPasswordRequestDto {

    @NotBlank
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,15}$")
    private String oldPassword;

    @NotBlank
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,15}$")
    private String newPassword;
}
