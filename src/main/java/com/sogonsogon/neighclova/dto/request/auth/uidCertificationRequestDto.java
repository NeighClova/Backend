package com.sogonsogon.neighclova.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class uidCertificationRequestDto {
    @NotBlank
    private String uid;
}
