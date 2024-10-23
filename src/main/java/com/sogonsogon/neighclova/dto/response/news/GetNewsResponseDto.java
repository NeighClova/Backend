package com.sogonsogon.neighclova.dto.response.news;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class GetNewsResponseDto extends ResponseDto {
    private String title;
    private String content;
    private String keyword;
    private LocalDateTime createdAt;

    @Builder
    private GetNewsResponseDto(String title, String content, String keyword) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);

        this.title = title;
        this.content = content;
        this.keyword = keyword;
        this.createdAt = LocalDateTime.now();
    }

    public static ResponseEntity<GetNewsResponseDto> success(String title, String content, String keyword) {

        GetNewsResponseDto result = new GetNewsResponseDto(title, content, keyword);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
