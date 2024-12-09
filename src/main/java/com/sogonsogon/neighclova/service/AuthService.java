package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.dto.request.auth.*;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.dto.response.auth.*;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<? super EmailCheckResponseDto> emailCheck(EmailCheckRequestDto dto);

    ResponseEntity<? super EmailCertificationResponseDto> emailCertification(EmailCertificationRequestDto dto);

    ResponseEntity<? super CheckCertificationResponseDto> checkCertification(CheckCertificationRequestDto dto);

    ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto dto);

    ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto);

    ResponseEntity<? super TokenResponseDto> reissue(String refreshToken);

    ResponseEntity<ResponseDto> patchPassword(PatchPasswordRequestDto dto, String email);

    ResponseEntity<? super DeleteUserResponseDto> deleteUser(String email);

    ResponseEntity<ResponseDto> checkPassword(CheckPasswordRequestDto dto, String email);

    ResponseEntity<ResponseDto> checkSocial(String email);

    ResponseEntity<ResponseDto> checkId(CheckIdRequestDto dto);

    ResponseEntity<? super SendUidResponseDto> sendUidByEmail(EmailCheckRequestDto dto);

    ResponseEntity<? super EmailCertificationResponseDto> uidCertification(uidCertificationRequestDto dto);
}
