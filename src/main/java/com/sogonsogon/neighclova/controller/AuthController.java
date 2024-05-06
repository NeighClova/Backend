package com.sogonsogon.neighclova.controller;

import com.sogonsogon.neighclova.dto.request.CheckCertificationRequestDto;
import com.sogonsogon.neighclova.dto.request.EmailCertificationRequestDto;
import com.sogonsogon.neighclova.dto.request.EmailCheckRequestDto;
import com.sogonsogon.neighclova.dto.response.CheckCertificationResponseDto;
import com.sogonsogon.neighclova.dto.response.EmailCertificationResponseDto;
import com.sogonsogon.neighclova.dto.response.EmailCheckResponseDto;
import com.sogonsogon.neighclova.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/email-check")
    public ResponseEntity<? super EmailCheckResponseDto> emailCheck(@RequestBody @Valid EmailCheckRequestDto requestBody) {
        ResponseEntity<? super EmailCheckResponseDto> response = authService.emailCheck(requestBody);
        return response;
    }

    @PostMapping("/email-certification")
    public ResponseEntity<? super EmailCertificationResponseDto> emailCertification(@RequestBody @Valid EmailCertificationRequestDto requestBody) {
        ResponseEntity<? super EmailCertificationResponseDto> response = authService.emailCertification(requestBody);
        return response;
    }

    @PostMapping("/check-certification")
    public ResponseEntity<? super CheckCertificationResponseDto> checkCertification(@RequestBody @Valid CheckCertificationRequestDto requestBody) {
        ResponseEntity<? super CheckCertificationResponseDto> response = authService.checkCertification(requestBody);
        return response;
    }
}
