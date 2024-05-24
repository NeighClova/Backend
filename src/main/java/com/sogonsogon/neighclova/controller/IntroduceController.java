package com.sogonsogon.neighclova.controller;

import com.sogonsogon.neighclova.dto.request.introduce.CreateIntroduceRequestDto;
import com.sogonsogon.neighclova.dto.response.introduce.GetIntroduceResponseDto;
import com.sogonsogon.neighclova.service.IntroduceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/introduce")
@RequiredArgsConstructor
@Slf4j
public class IntroduceController {

    private final IntroduceService introduceService;

    // 키워드 기반 맞춤 소식 글 생성하기
    @PostMapping("/ai")
    public ResponseEntity<? super GetIntroduceResponseDto> createIntroduce(@RequestParam("placeId") Long placeId, @RequestBody CreateIntroduceRequestDto requestDto) {
        ResponseEntity<? super GetIntroduceResponseDto> response = introduceService.createIntroduce(placeId, requestDto);
        return response;
    }
}
