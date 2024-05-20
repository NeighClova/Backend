package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.domain.News;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.dto.object.NewsListItem;
import com.sogonsogon.neighclova.dto.request.news.CreateNewsRequestDto;
import com.sogonsogon.neighclova.dto.request.news.NewsMessageRequestDto;
import com.sogonsogon.neighclova.dto.request.news.NewsRequestDto;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.dto.response.news.GetAllNewsResponseDto;
import com.sogonsogon.neighclova.dto.response.news.GetNewsResponseDto;
import com.sogonsogon.neighclova.dto.response.news.NewsResponseDto;
import com.sogonsogon.neighclova.repository.NewsRepository;
import com.sogonsogon.neighclova.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class NewsService extends ResponseDto {

    @Value("${X_API_KEY}")
    private String X_API_KEY;

    @Value("${X_API_KEY_PRIMARY}")
    private String X_API_KEY_PRIMARY;

    @Value("${X_REQUEST_ID}")
    private String X_REQUEST_ID;

    private static final String ENDPOINT = "https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-003";

    private final NewsRepository newsRepo;
    private final PlaceRepository placeRepo;

    @Transactional
    public ResponseEntity<? super GetAllNewsResponseDto> getAllNews(Long placeId, String email) {
        List<NewsListItem> newsListItems = new ArrayList<>();
        try {
            Place place = placeRepo.findById(placeId).orElse(null);
            if (place != null & email.equals(place.getUserId().getEmail())) {
                List<News> newsList = newsRepo.findAllByPlaceId(place);

                for (News news : newsList)
                    newsListItems.add(NewsListItem.of(news));
            }


        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        GetAllNewsResponseDto responseDto = new GetAllNewsResponseDto(newsListItems);
        return responseDto.success(newsListItems);
    }

    @Transactional
    public ResponseEntity<? super NewsResponseDto> saveNews(String email, NewsRequestDto requestDto) {
        try {
            Place place = placeRepo.findById(requestDto.getPlaceId()).orElseThrow(() -> new NoSuchElementException("Place not found"));

            // Place가 사용자 가게가 아니면 noPermission
            if (email.equals(place.getUserId().getEmail())) {
                News news = requestDto.toEntity(place);
                newsRepo.save(news);
            } else {
                return NewsResponseDto.noPermission();
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return NewsResponseDto.success();
    }

    @Transactional
    public ResponseEntity<? super GetNewsResponseDto> createNews(Long placeId, CreateNewsRequestDto requestDto) {
        Place place;
        String title;
        String content;
        try {

            place = placeRepo.findById(placeId).get();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-NCP-CLOVASTUDIO-API-KEY", X_API_KEY);
            headers.set("X-NCP-APIGW-API-KEY", X_API_KEY_PRIMARY);
            headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", X_REQUEST_ID);

            List<NewsMessageRequestDto> messages = new ArrayList<>();
            String prompt = String.format(
                    "- 가게 명 : %s\n" +
                            "- 가게 소식 키워드 : %s\n" +
                            "- 소식 유형 : %s\n" +
                            "- 추가 소식 유형 : %s\n" +
                            "- 기간 : %s ~ %s\n" +
                            "- 강조 내용 : %s",
                    place.getPlaceName(), requestDto.getKeyword(), requestDto.getNewsType(),
                    requestDto.getNewsDetail(), requestDto.getStartDate(), requestDto.getEndDate(),
                    requestDto.getHighlightContent()
            );

            messages.add(new NewsMessageRequestDto("user", prompt));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messages", messages);
            requestBody.put("content", prompt);
            requestBody.put("topP", 0.8);
            requestBody.put("topK", 0);
            requestBody.put("maxTokens", 256);
            requestBody.put("temperature", 0.5);
            requestBody.put("repeatPenalty", 5.0);
            requestBody.put("stopBefore", Collections.emptyList());
            requestBody.put("includeAiFilters", true);
            requestBody.put("seed", 0);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(ENDPOINT, requestEntity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            Map<String, Object> result = (Map<String, Object>) responseBody.get("result");
            String responseContent = (String) ((Map<String, Object>) result.get("message")).get("content");

            // title과 content 분리
            String[] splitResponse = responseContent.split("\n\n");
            title = splitResponse.length > 0 ? splitResponse[0] : "";
            content = splitResponse.length > 1 ? String.join("\n\n", Arrays.copyOfRange(splitResponse, 1, splitResponse.length)) : "";

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetNewsResponseDto.success(title, content, requestDto.getKeyword());
    }
}