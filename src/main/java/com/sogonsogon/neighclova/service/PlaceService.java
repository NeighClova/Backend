package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.domain.Feedback;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.domain.User;
import com.sogonsogon.neighclova.dto.object.PlaceListItem;
import com.sogonsogon.neighclova.dto.request.place.PlaceRequestDto;
import com.sogonsogon.neighclova.dto.request.place.ProfileImgRequestDto;
import com.sogonsogon.neighclova.dto.response.place.GetAllPlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.place.GetPlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.place.PlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.repository.FeedbackRepository;
import com.sogonsogon.neighclova.repository.PlaceRepository;
import com.sogonsogon.neighclova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaceService {

    private final UserRepository userRepo;
    private final PlaceRepository placeRepo;
    private final FeedbackRepository feedbackRepo;

    @Transactional
    public ResponseEntity<? super PlaceResponseDto> savePlace(String email, PlaceRequestDto dto) {
        try {
            User user = userRepo.findByEmail(email);
            if (user != null) {
                Place place = dto.toEntity(user);

                // place 저장 후 place 반환
                Place savedPlace = placeRepo.save(place);

                int remainder = (int) ((savedPlace.getPlaceId()) % 7);
                String dayOfWeek = getDayOfWeek(remainder);

                Feedback feedback = Feedback.builder()
                        .placeId(savedPlace)
                        .viewDate(dayOfWeek)
                        .createdAt(LocalDateTime.now())
                        .build();

                feedbackRepo.save(feedback);
            } else {
                return PlaceResponseDto.notExistUser();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PlaceResponseDto.success();
    }

    @Transactional
    public ResponseEntity<? super PlaceResponseDto> patchPlace(Long placeId, String email, PlaceRequestDto dto) {
        try {
            Optional<Place> placeOptional = placeRepo.findById(placeId);
            if (!placeOptional.isPresent()) return PlaceResponseDto.notExistedPlace();

            Place place = placeOptional.get();
            User user = userRepo.findByEmail(email);
            Long ownerId = place.getUserId().getUserId();

            if (!ownerId.equals(user.getUserId())) return PlaceResponseDto.noPermission();

            place.patchPlace(dto, user);
            placeRepo.save(place);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PlaceResponseDto.success();
    }

    @Transactional
    public ResponseEntity<? super PlaceResponseDto> patchProfileImg(Long placeId, String email, ProfileImgRequestDto dto) {
        try {
            Optional<Place> placeOptional = placeRepo.findById(placeId);
            if (!placeOptional.isPresent()) return PlaceResponseDto.notExistedPlace();

            Place place = placeOptional.get();
            User user = userRepo.findByEmail(email);
            Long ownerId = place.getUserId().getUserId();

            if (!ownerId.equals(user.getUserId())) return PlaceResponseDto.noPermission();

            place.patchProfileImg(dto);
            placeRepo.save(place);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PlaceResponseDto.success();
    }

    @Transactional
    public ResponseEntity<? super GetAllPlaceResponseDto> getAllPlace(String email) {
        List<PlaceListItem> placeListItems = new ArrayList<>();
        try {
            User user = userRepo.findByEmail(email);
            List<Place> places = placeRepo.findAllByUserId(user);

            for (Place place : places)
                placeListItems.add(PlaceListItem.of(place));

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        GetAllPlaceResponseDto responseDto = new GetAllPlaceResponseDto(placeListItems);
        return responseDto.success(placeListItems);
    }

    @Transactional
    public ResponseEntity<? super GetPlaceResponseDto> getPlace(Long placeId) {
        Place place;
        try {
            if (placeRepo.existsById(placeId)) {
                place = placeRepo.findById(placeId).get();
            } else {
                return GetPlaceResponseDto.noExistPlace();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetPlaceResponseDto.success(place);
    }

    public static String getDayOfWeek(int remainder) {
        Map<Integer, String> dayMap = new HashMap<>();
        dayMap.put(0, "일요일");
        dayMap.put(1, "월요일");
        dayMap.put(2, "화요일");
        dayMap.put(3, "수요일");
        dayMap.put(4, "목요일");
        dayMap.put(5, "금요일");
        dayMap.put(6, "토요일");

        return dayMap.getOrDefault(remainder, "Invalid");
    }
}
