package com.sogonsogon.neighclova.dto.response.introduce;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetIntroduceResponseDto extends ResponseDto {

    private String content;

    @Builder
    private GetIntroduceResponseDto(String content) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);

        this.content = content;
    }

    public static ResponseEntity<GetIntroduceResponseDto> success(String content) {

        GetIntroduceResponseDto result = new GetIntroduceResponseDto(content);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
