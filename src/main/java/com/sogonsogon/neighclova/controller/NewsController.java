package com.sogonsogon.neighclova.controller;


import com.sogonsogon.neighclova.dto.request.news.CreateNewsRequestDto;
import com.sogonsogon.neighclova.dto.request.news.NewsRequestDto;
import com.sogonsogon.neighclova.dto.response.news.GetAllNewsResponseDto;
import com.sogonsogon.neighclova.dto.response.news.GetNewsResponseDto;
import com.sogonsogon.neighclova.dto.response.news.NewsResponseDto;
import com.sogonsogon.neighclova.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Slf4j
public class NewsController {

    private final NewsService newsService;

    // 전체 조회
    @GetMapping("/all")
    public ResponseEntity<? super GetAllNewsResponseDto> getAllNews(@RequestParam("placeId") Long placeId) {
        String email = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                email = authentication.getName();
            }

            if (email == null)
                return NewsResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return NewsResponseDto.databaseError();
        }
        ResponseEntity<? super GetAllNewsResponseDto> response = newsService.getAllNews(placeId, email);
        return response;
    }

    // 저장
    @PostMapping("")
    public ResponseEntity<? super NewsResponseDto> saveNews(@RequestBody NewsRequestDto requestDto) {
        String email = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                email = authentication.getName();
            }

            if (email == null)
                return NewsResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return NewsResponseDto.databaseError();
        }

        ResponseEntity<? super NewsResponseDto> response = newsService.saveNews(email, requestDto);
        return response;
    }

    // 키워드 기반 맞춤 소식 글 생성하기
    @PostMapping("/ai")
    public ResponseEntity<? super GetNewsResponseDto> createNews(@RequestParam("placeId") Long placeId, @RequestBody CreateNewsRequestDto requestDto) {
        ResponseEntity<? super GetNewsResponseDto> response = newsService.createNews(placeId, requestDto);
        return response;
    }
}
