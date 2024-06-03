package com.sogonsogon.neighclova.dto.response.main;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.domain.Feedback;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.dto.response.feedback.GetFeedbackResponseDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Getter
public class GetMainResponseDto extends ResponseDto {
    private Long placeId;
    private String placeName;
    private List<String> keyword;
    private String pBody;
    private String nBody;
    private String elapsedTime;

    @Builder
    private GetMainResponseDto(Feedback feedback, String elapsedTime) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);

        if(feedback.getKeyword() != null) {
            List<String> keyword = Arrays.asList(feedback.getKeyword().split(","));

            this.placeId = feedback.getPlaceId().getPlaceId();
            this.placeName = feedback.getPlaceId().getPlaceName();
            this.pBody = feedback.getPBody();
            this.nBody = feedback.getNBody();
            this.keyword = keyword;
            this.elapsedTime = elapsedTime;
        } else {
            this.placeId = feedback.getPlaceId().getPlaceId();
            this.placeName = feedback.getPlaceId().getPlaceName();
            this.pBody = null;
            this.nBody = null;
            this.keyword = null;
            this.elapsedTime = elapsedTime;
        }
    }

    public static ResponseEntity<GetMainResponseDto> success(Feedback feedback, String elapsedTime) {

        GetMainResponseDto result = new GetMainResponseDto(feedback, elapsedTime);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
