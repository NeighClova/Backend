package com.sogonsogon.neighclova.dto.response.place;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

@Getter
public class GetPlaceResponseDto extends ResponseDto {
    private Long placeId;
    private String placeName;
    private String category;
    private String placeUrl;
    private String profileImg;
    private List<String> targetAge;
    private List<String> target;

    @Builder
    private GetPlaceResponseDto(Place place) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        List<String> target = Arrays.asList(place.getTarget().split("\\s*,\\s*"));
        List<String> targetAge = Arrays.asList(place.getTargetAge().split("\\s*,\\s*"));

        this.placeId = place.getPlaceId();
        this.placeName = place.getPlaceName();
        this.category = place.getCategory();
        this.placeUrl = place.getPlaceUrl();
        this.profileImg = place.getProfileImg();
        this.targetAge = targetAge;
        this.target = target;
    }

    public static ResponseEntity<GetPlaceResponseDto> success(Place place) {

        GetPlaceResponseDto result = new GetPlaceResponseDto(place);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ResponseDto> noExistPlace() {
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXISTED_PLACE, ResponseMessage.NOT_EXISTED_PLACE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
