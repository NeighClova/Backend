package com.sogonsogon.neighclova.dto.response.auth;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class SignInResponseDto extends ResponseDto {
    private String accessToken;
    private String refreshToken;
    private int expirationTime;
    private String email;

    private SignInResponseDto(String accessToken, String refreshToken, String email) {
        super();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationTime = 3600;
        this.email = email;
    }

    public static ResponseEntity<SignInResponseDto> success(String accessToken, String refreshToken, String email) {
        SignInResponseDto responseBody = new SignInResponseDto(accessToken, refreshToken, email);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> signInFail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.SIGN_IN_FAILED, ResponseMessage.SIGN_IN_FAILED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }
}
