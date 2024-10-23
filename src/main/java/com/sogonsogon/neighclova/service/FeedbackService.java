package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.domain.Feedback;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.dto.response.feedback.FeedbackResponseDto;
import com.sogonsogon.neighclova.dto.response.feedback.GetFeedbackResponseDto;
import com.sogonsogon.neighclova.repository.FeedbackRepository;
import com.sogonsogon.neighclova.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class FeedbackService extends ResponseDto {

    private final FeedbackRepository feedbackRepo;
    private final PlaceRepository placeRepo;

    @Transactional
    public ResponseEntity<? super GetFeedbackResponseDto> getFeedback(Long placeId, String email) {
        Feedback feedback;
        try {
            Place place = placeRepo.findById(placeId).orElse(null);
            if (place != null & email.equals(place.getUserId().getEmail())) {
                feedback = feedbackRepo.findTop1ByPlaceIdEqualsOrderByCreatedAtDesc(place);
            }
            else {
                return FeedbackResponseDto.noPermission();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetFeedbackResponseDto.success(feedback);
    }
}
