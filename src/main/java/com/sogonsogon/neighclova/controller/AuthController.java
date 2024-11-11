package com.sogonsogon.neighclova.controller;

import com.sogonsogon.neighclova.dto.request.auth.*;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.dto.response.auth.*;
import com.sogonsogon.neighclova.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/email-check")
    public ResponseEntity<? super EmailCheckResponseDto> emailCheck(
            @RequestBody @Valid EmailCheckRequestDto requestBody) {
        ResponseEntity<? super EmailCheckResponseDto> response = authService.emailCheck(requestBody);
        return response;
    }

    @PostMapping("/email-certification")
    public ResponseEntity<? super EmailCertificationResponseDto> emailCertification(
            @RequestBody @Valid EmailCertificationRequestDto requestBody) {
        ResponseEntity<? super EmailCertificationResponseDto> response = authService.emailCertification(requestBody);
        return response;
    }

    @PostMapping("/check-certification")
    public ResponseEntity<? super CheckCertificationResponseDto> checkCertification(
            @RequestBody @Valid CheckCertificationRequestDto requestBody) {
        ResponseEntity<? super CheckCertificationResponseDto> response = authService.checkCertification(requestBody);
        return response;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<? super SignUpResponseDto> signUp(@RequestBody @Valid SignUpRequestDto requestBody) {
        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestBody);
        return response;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<? super SignInResponseDto> signIn(@RequestBody @Valid SignInRequestDto requestBody) {
        ResponseEntity<? super SignInResponseDto> response = authService.signIn(requestBody);
        return response;
    }

    @PostMapping("/reissue")
    public ResponseEntity<? super TokenResponseDto> reissueToken(@RequestHeader(value = "refreshToken") String refreshToken) {
        ResponseEntity<? super TokenResponseDto> response = authService.reissue(refreshToken);
        return response;
    }

    @PatchMapping("/patch-password")
    public ResponseEntity<ResponseDto> patchPassword(
            @RequestBody @Valid PatchPasswordRequestDto requestBody) {
        String email = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                email = authentication.getName();
            }

            if (email == null)
                return ResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return ResponseDto.databaseError();
        }

        ResponseEntity<ResponseDto> response = authService.patchPassword(requestBody, email);
        return response;
    }

    @PatchMapping("/delete")
    public ResponseEntity<? super DeleteUserResponseDto> deleteUser() {
        String email = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                email = authentication.getName();
            }

            if (email == null)
                return DeleteUserResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return DeleteUserResponseDto.databaseError();
        }

        ResponseEntity<? super DeleteUserResponseDto> response = authService.deleteUser(email);
        return response;
    }

    @PostMapping("/check-password")
    public ResponseEntity<ResponseDto> checkPassword(
            @RequestBody @Valid CheckPasswordRequestDto requestBody) {
        String email = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                email = authentication.getName();
            }

            if (email == null)
                return ResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return ResponseDto.databaseError();
        }

        ResponseEntity<ResponseDto> response = authService.checkPassword(requestBody, email);
        return response;
    }

    @GetMapping("/check-social")
    public ResponseEntity<ResponseDto> checkSocial() {
        String email = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                email = authentication.getName();
            }

            if (email == null)
                return ResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return ResponseDto.databaseError();
        }

        ResponseEntity<ResponseDto> response = authService.checkSocial(email);
        return response;
    }

    @PostMapping("/check-id")
    public ResponseEntity<ResponseDto> checkId(@RequestBody @Valid CheckIdRequestDto requestBody) {
        ResponseEntity<ResponseDto> response = authService.checkId(requestBody);
        return response;
    }

    // 이메일로 아이디 찾기
    @PostMapping("/send-uid")
    public ResponseEntity<? super SendUidResponseDto> sendUidByEmail(@RequestBody @Valid EmailCheckRequestDto requestBody) {
        ResponseEntity<? super SendUidResponseDto> response = authService.sendUidByEmail(requestBody);
        return response;
    }
}
