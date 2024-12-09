package com.sogonsogon.neighclova.dto.response.auth;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class SendUidResponseDto extends ResponseDto {
    private SendUidResponseDto() {
        super();
    }

    public static ResponseEntity<SendUidResponseDto> success() {
        SendUidResponseDto responseBody = new SendUidResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> notExistedEmail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.NOT_EXISTED_EMAIL, ResponseMessage.NOT_EXISTED_EMAIL);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> mailSendFail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.MAIL_FAIL, ResponseMessage.MAIL_FAIL);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }
}
