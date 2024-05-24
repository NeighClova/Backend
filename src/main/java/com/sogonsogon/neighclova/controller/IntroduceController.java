package com.sogonsogon.neighclova.controller;

import com.sogonsogon.neighclova.dto.request.introduce.CreateIntroduceRequestDto;
import com.sogonsogon.neighclova.dto.request.introduce.IntroduceRequestDto;
import com.sogonsogon.neighclova.dto.response.introduce.GetIntroduceResponseDto;
import com.sogonsogon.neighclova.dto.response.introduce.IntroduceResponseDto;
import com.sogonsogon.neighclova.service.IntroduceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/introduce")
@RequiredArgsConstructor
@Slf4j
public class IntroduceController {

    private final IntroduceService introduceService;

    // 저장
    @PostMapping("")
    public ResponseEntity<? super IntroduceResponseDto> saveIntroduce(@RequestBody IntroduceRequestDto requestDto) {
        String email = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                email = authentication.getName();
            }

            if (email == null)
                return IntroduceResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return IntroduceResponseDto.databaseError();
        }

        ResponseEntity<? super IntroduceResponseDto> response = introduceService.saveIntroduce(email, requestDto);
        return response;
    }

    // 키워드 기반 맞춤 소식 글 생성하기
    @PostMapping("/ai")
    public ResponseEntity<? super GetIntroduceResponseDto> createIntroduce(@RequestParam("placeId") Long placeId, @RequestBody CreateIntroduceRequestDto requestDto) {
        ResponseEntity<? super GetIntroduceResponseDto> response = introduceService.createIntroduce(placeId, requestDto);
        return response;
    }
}
