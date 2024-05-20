package com.sogonsogon.neighclova.dto.response.news;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.dto.object.NewsListItem;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetAllNewsResponseDto extends ResponseDto {
    private List<NewsListItem> newsList;

    public GetAllNewsResponseDto(List<NewsListItem> newsListItem) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.newsList = newsListItem;
    }

    public static ResponseEntity<GetAllNewsResponseDto> success(List<NewsListItem> newsListItem) {
        GetAllNewsResponseDto result = new GetAllNewsResponseDto(newsListItem);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
