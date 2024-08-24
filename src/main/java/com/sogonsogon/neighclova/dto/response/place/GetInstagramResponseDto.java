package com.sogonsogon.neighclova.dto.response.place;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetInstagramResponseDto extends ResponseDto{
    private Long placeId;
    private String instagramId;
    private String instagramPw;

    @Builder
    private GetInstagramResponseDto(Long placeId, String id, String password) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);

        this.placeId = placeId;
        this.instagramId = id;
        this.instagramPw = password;
    }

    public static ResponseEntity<GetInstagramResponseDto> success(Long placeId, String id, String password) {

        GetInstagramResponseDto result = new GetInstagramResponseDto(placeId, id, password);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ResponseDto> noExistPlace() {
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXISTED_PLACE, ResponseMessage.NOT_EXISTED_PLACE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
