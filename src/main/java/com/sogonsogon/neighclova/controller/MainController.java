package com.sogonsogon.neighclova.controller;

import com.sogonsogon.neighclova.dto.response.feedback.FeedbackResponseDto;
import com.sogonsogon.neighclova.dto.response.feedback.GetFeedbackResponseDto;
import com.sogonsogon.neighclova.dto.response.main.GetMainResponseDto;
import com.sogonsogon.neighclova.dto.response.main.MainResponseDto;
import com.sogonsogon.neighclova.service.FeedbackService;
import com.sogonsogon.neighclova.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final MainService mainService;

    // 전체 조회
    @GetMapping("")
    public ResponseEntity<? super GetMainResponseDto> getAllNews(@RequestParam("placeId") Long placeId) {
        String email = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                email = authentication.getName();
            }

            if (email == null)
                return MainResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return MainResponseDto.databaseError();
        }
        ResponseEntity<? super GetMainResponseDto> response = mainService.getMain(placeId, email);
        return response;
    }
}
