package com.sogonsogon.neighclova.dto.response.feedback;

import com.sogonsogon.neighclova.common.ResponseCode;
import com.sogonsogon.neighclova.common.ResponseMessage;
import com.sogonsogon.neighclova.domain.Feedback;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Getter
public class GetFeedbackResponseDto extends ResponseDto {

    private Long placeId;
    private List<String> pSummary;
    private String pBody;
    private List<String> nSummary;
    private String nBody;
    private List<String> keyword;
    private String createdAt;
    private String updatedAt;
    private String viewDate;

    @Builder
    private GetFeedbackResponseDto(Feedback feedback) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        String createdAtStr = feedback.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if (feedback.getKeyword() != null) {
            List<String> strpSummary = Arrays.asList(feedback.getPSummary().split("\\.\\s*"));
            List<String> strnSummary = Arrays.asList(feedback.getNSummary().split("\\.\\s*"));
            List<String> keyword = Arrays.asList(feedback.getKeyword().split(","));
            String updatedAtStr = feedback.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            this.placeId = feedback.getPlaceId().getPlaceId();
            this.pSummary = strpSummary;
            this.pBody = feedback.getPBody();
            this.nSummary = strnSummary;
            this.nBody = feedback.getNBody();
            this.keyword = keyword;
            this.createdAt = createdAtStr;
            this.updatedAt = updatedAtStr;
            this.viewDate = feedback.getViewDate();
        } else {
            this.placeId = feedback.getPlaceId().getPlaceId();
            this.pSummary = null;
            this.pBody = null;
            this.nSummary = null;
            this.keyword = null;
            this.createdAt = createdAtStr;
            this.updatedAt = null;
            this.viewDate = feedback.getViewDate();
        }
    }

    public static ResponseEntity<GetFeedbackResponseDto> success(Feedback feedback) {

        GetFeedbackResponseDto result = new GetFeedbackResponseDto(feedback);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
