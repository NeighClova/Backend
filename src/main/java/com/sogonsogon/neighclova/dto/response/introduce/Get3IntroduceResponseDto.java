package com.sogonsogon.neighclova.dto.response.introduce;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.dto.object.IntroduceListItem;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class Get3IntroduceResponseDto extends ResponseDto {
    private Long placeId;
    private String placeName;
    private String placeCategory;
    private String placeProfileImg;
    private List<IntroduceListItem> introduceList;

    public Get3IntroduceResponseDto(List<IntroduceListItem> introduceList, Place place) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.placeId = place.getPlaceId();
        this.placeName = place.getPlaceName();
        this.placeCategory = place.getCategory();
        this.placeProfileImg = place.getProfileImg();
        this.introduceList = introduceList;
    }

    public static ResponseEntity<Get3IntroduceResponseDto> success(List<IntroduceListItem> introduceList, Place place) {
        Get3IntroduceResponseDto result = new Get3IntroduceResponseDto(introduceList, place);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
