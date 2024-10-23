package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.domain.Feedback;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.dto.response.main.GetMainResponseDto;
import com.sogonsogon.neighclova.dto.response.main.MainResponseDto;
import com.sogonsogon.neighclova.repository.FeedbackRepository;
import com.sogonsogon.neighclova.repository.NewsRepository;
import com.sogonsogon.neighclova.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Service
public class MainService extends ResponseDto {

    private final FeedbackRepository feedbackRepo;
    private final PlaceRepository placeRepo;
    private final NewsRepository newsRepo;

    @Transactional
    public ResponseEntity<? super GetMainResponseDto> getMain(Long placeId, String email) {
        Feedback feedback;
        String elapsedTimeString;
        try {
            Place place = placeRepo.findById(placeId).orElse(null);
            if (place != null & email.equals(place.getUserId().getEmail())) {
                feedback = feedbackRepo.findTop1ByPlaceIdEqualsOrderByCreatedAtDesc(place);

                LocalDateTime newsCreatedAt = newsRepo.findCreatedAtByPlaceIdEqualsOrderByCreatedAtDesc(place);
                if (newsCreatedAt == null) {
                    elapsedTimeString = "null";
                } else {
                    Duration elapsedTime = Duration.between(newsCreatedAt, LocalDateTime.now());
                    elapsedTimeString = formatDuration(elapsedTime);
                }

            }
            else {
                return MainResponseDto.noPermission();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetMainResponseDto.success(feedback, elapsedTimeString);
    }

    private static String formatDuration(Duration duration) {
        long days = duration.toDays();
        duration = duration.minusDays(days);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long seconds = duration.getSeconds();

        return String.format("%d 일, %d 시간, %d 분, %d 초",
                days, hours, minutes, seconds);
    }
}
