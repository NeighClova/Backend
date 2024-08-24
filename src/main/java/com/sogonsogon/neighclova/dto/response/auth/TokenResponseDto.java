package com.sogonsogon.neighclova.dto.response.auth;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class TokenResponseDto extends ResponseDto {
    private String accessToken;
    private String refreshToken;
    private int expirationTime;

    private TokenResponseDto(String accessToken, String refreshToken) {
        super();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationTime = 3600;
    }

    public static ResponseEntity<TokenResponseDto> success(String accessToken, String refreshToken) {
        TokenResponseDto responseBody = new TokenResponseDto(accessToken, refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> reissuedFail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.INVALID_GRANT, ResponseMessage.INVALID_GRANT);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }
}
