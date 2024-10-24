package com.sogonsogon.neighclova.controller;

import com.sogonsogon.neighclova.dto.request.place.InstagramRequestDto;
import com.sogonsogon.neighclova.dto.request.place.PlaceRequestDto;
import com.sogonsogon.neighclova.dto.request.place.UploadInstagramRequestDto;
import com.sogonsogon.neighclova.dto.response.place.GetAllPlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.place.GetInstagramResponseDto;
import com.sogonsogon.neighclova.dto.response.place.GetPlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.place.PlaceResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.sogonsogon.neighclova.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // 프로필 사진 수정
    @PatchMapping("/img")
    public ResponseEntity<? super PlaceResponseDto> patchProfileImg(@RequestParam("placeId") Long placeId, @RequestParam(value = "file") MultipartFile file) {
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

        ResponseEntity<? super PlaceResponseDto> response = placeService.patchProfileImg(placeId, email, file);
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

    // 개별 조회
    @GetMapping("")
    public ResponseEntity<? super GetPlaceResponseDto> getPlace(@RequestParam("placeId") Long placeId) {
        ResponseEntity<? super GetPlaceResponseDto> response = placeService.getPlace(placeId);
        return response;
    }

    // 인스타그램 계정 연결
    @PostMapping("/instagram")
    public ResponseEntity<? super PlaceResponseDto> saveInstagram(@RequestBody InstagramRequestDto requestDto) {
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

        ResponseEntity<? super PlaceResponseDto> response = placeService.saveInstagram(email, requestDto);
        return response;
    }

    // 인스타그램 계정 정보 수정
    @PatchMapping("/instagram")
    public ResponseEntity<? super PlaceResponseDto> patchInstagram(@RequestBody InstagramRequestDto requestDto) {
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

        ResponseEntity<? super PlaceResponseDto> response = placeService.patchInstagram(email, requestDto);
        return response;
    }

    // 인스타그램 계정 정보 불러오기
    @GetMapping("/instagram")
    public ResponseEntity<? super GetInstagramResponseDto> getInstagram(@RequestParam("placeId") Long placeId) {
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

        ResponseEntity<? super GetInstagramResponseDto> response = placeService.getInstagram(email, placeId);
        return response;
    }

    // 인스타그램 자동 업로드
    @PostMapping("/instagram/upload")
    public ResponseEntity<? super PlaceResponseDto> uploadInstagram(@RequestPart(value = "dto") UploadInstagramRequestDto dto,
                                                                    @RequestPart(value = "file") MultipartFile file) {
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

        ResponseEntity<? super PlaceResponseDto> response = placeService.uploadInstagram(email, dto, file);
        return response;
    }
}
