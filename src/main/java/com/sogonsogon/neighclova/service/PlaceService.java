package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.domain.User;
import com.sogonsogon.neighclova.dto.object.PlaceListItem;
import com.sogonsogon.neighclova.dto.request.PlaceRequestDto;
import com.sogonsogon.neighclova.dto.response.GetAllPlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.GetPlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.PlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.repository.PlaceRepository;
import com.sogonsogon.neighclova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaceService {

    private final UserRepository userRepo;
    private final PlaceRepository placeRepo;

    @Transactional
    public ResponseEntity<? super PlaceResponseDto> savePlace(String email, PlaceRequestDto dto) {
        try {
            User user = userRepo.findByEmail(email);
            if (user != null) {
                Place place = dto.toEntity(user);
                placeRepo.save(place);
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
}
