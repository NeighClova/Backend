package com.sogonsogon.neighclova.controller;

import com.sogonsogon.neighclova.dto.request.PlaceRequestDto;
import com.sogonsogon.neighclova.dto.response.GetAllPlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.PlaceResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.sogonsogon.neighclova.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
@Slf4j
public class PlaceController {

    private final PlaceService placeService;

    // 저장
    @PostMapping("")
    public ResponseEntity<? super PlaceResponseDto> save(@RequestBody PlaceRequestDto requestDto) {
        String email = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                email = authentication.getName();
            }

            if (email == null)
                return PlaceResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return PlaceResponseDto.databaseError();
        }

        ResponseEntity<? super PlaceResponseDto> response = placeService.savePlace(email, requestDto);
        return response;
    }

    // 수정
    @PatchMapping("")
    public ResponseEntity<? super PlaceResponseDto> patchPlace(@RequestParam("placeId") Long placeId, @RequestBody PlaceRequestDto requestDto) {
        String email = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                email = authentication.getName();
            }

            if (email == null)
                return PlaceResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return PlaceResponseDto.databaseError();
        }

        ResponseEntity<? super PlaceResponseDto> response = placeService.patchPlace(placeId, email, requestDto);
        return response;
    }

    // 전체 조회
    @GetMapping("/all")
    public ResponseEntity<? super GetAllPlaceResponseDto> getAllPlace() {
        String email = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                email = authentication.getName();
            }

            if (email == null)
                return PlaceResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return PlaceResponseDto.databaseError();
        }
        ResponseEntity<? super GetAllPlaceResponseDto> response = placeService.getAllPlace(email);
        return response;
    }


}
