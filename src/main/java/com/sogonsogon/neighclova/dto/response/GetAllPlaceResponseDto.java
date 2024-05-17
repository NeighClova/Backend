package com.sogonsogon.neighclova.dto.response;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.dto.object.PlaceListItem;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetAllPlaceResponseDto extends ResponseDto{
    private List<PlaceListItem> placeList;

    public GetAllPlaceResponseDto(List<PlaceListItem> PlaceListItem) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.placeList = PlaceListItem;
    }

    public static ResponseEntity<GetAllPlaceResponseDto> success(List<PlaceListItem> PlaceListItem) {
        GetAllPlaceResponseDto result = new GetAllPlaceResponseDto(PlaceListItem);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ResponseDto> notExistedPlace(){
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXISTED_PLACE, ResponseMessage.NOT_EXISTED_PLACE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
