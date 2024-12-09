package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.domain.News;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.dto.object.NewsListItem;
import com.sogonsogon.neighclova.dto.request.MessageRequestDto;
import com.sogonsogon.neighclova.dto.request.news.CreateNewsRequestDto;
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

    @Value("${X_NCP_CLOVASTUDIO_NEWS_REQUEST_ID}")
    private String X_NCP_CLOVASTUDIO_NEWS_REQUEST_ID;

    @Value("${X_NCP_CLOVASTUDIO_API_KEY}")
    private String X_NCP_CLOVASTUDIO_API_KEY;

    @Value("${X_NCP_APIGW_API_KEY}")
    private String X_NCP_APIGW_API_KEY;

    @Value("${NEWS_ENDPOINT}")
    private String NEWS_ENDPOINT;

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
            else {
                return NewsResponseDto.noPermission();
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
            headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", X_NCP_CLOVASTUDIO_NEWS_REQUEST_ID);
            headers.set("X-NCP-CLOVASTUDIO-API-KEY", X_NCP_CLOVASTUDIO_API_KEY);
            headers.set("X-NCP-APIGW-API-KEY", X_NCP_APIGW_API_KEY);

            List<MessageRequestDto> messages = new ArrayList<>();
            String message = String.format(
                    "- 매장명: %s\n" +
                            "- 매장 소식 키워드: %s\n" +
                            "- 소식 유형: %s\n" +
                            "- 추가 소식 유형: %s\n" +
                            "- 기간: %s ~ %s\n" +
                            "- 강조 내용: %s\n" +
                            "- 타겟 연령대: %s\n" +
                            "- 타겟 대상: %s\n",
                    place.getPlaceName(), requestDto.getKeyword(), requestDto.getNewsType(),
                    requestDto.getNewsDetail(), requestDto.getStartDate(), requestDto.getEndDate(),
                    requestDto.getHighlightContent(), place.getTargetAge(), place.getTarget()
            );

            //String prompt = "- 키워드를 기반으로 매장의 소식 글을 생성한다.\n- 기간, 강조 내용, 추가 소식 유형은 내용이 없을 수도 있는데, 임의로 글을 작성하지 말고 무시한다.\n- 출력 시 제목\\n\\n내용 형식으로 출력한다.\n\n예시)\n- 매장명: 공릉동 닭칼국수\r\n- 매장 소식 키워드: 임시 휴무\n- 소식 유형: 매장 이용 안내\n- 추가 소식 유형: 개인 일정으로 임시 휴업합니다.\n- 기간: 10월 23일 (수) 오전 9:00 ~ 10월 27일 (일) 오후 10:00\n- 강조 내용: 임시 휴무\n- 타겟 연령대: 20대, 30대\n- 타겟 대상: 대학생, 가족, 직장인\n\n공릉동 닭칼국수 임시 휴무 안내\n\n안녕하세요, 공릉동 닭칼국수입니다. 고객님들께 안내드립니다.\n저희 매장은 개인 일정으로 인해 임시 휴무를 하게 되었습니다.\n이용에 참고 부탁드리며, 불편을 드려 죄송합니다.\n\n기간: 10월 23일 (수) 오전 9:00 ~ 10월 27일 (일) 오후 10:00";
            // 1. 목표 제시, 2. 형식 제시, 3. 조건과 예외사항 추가 (특정 조건 없으면 다른 대안 제시), 4. 예시 제공
            String prompt =
                    "다음 정보를 기반으로 매장의 소식을 작성하세요.\n" +
                            "- 반드시 '제목\\n\\n내용' 형식으로 출력하세요.\n" +
                            "- '기간', '강조 내용', '추가 소식' 정보가 없으면 해당 부분을 작성하지 마세요.\n" +
                            "- 타겟 연령대와 타겟 대상에 맞춰 친근한 톤으로 작성하세요.\n\n" +
                            "입력 예시:\n" +
                            "- 매장명: 공릉동 닭칼국수\n" +
                            "- 키워드: 임시 휴무\n" +
                            "- 소식 유형: 매장 이용 안내\n" +
                            "- 추가 소식: 개인 일정으로 인한 임시 휴업\n" +
                            "- 기간: 10월 23일 (수) 오전 9:00 ~ 10월 27일 (일) 오후 10:00\n" +
                            "- 강조 내용: 임시 휴무\n" +
                            "- 타겟 연령대: 20대, 30대\n" +
                            "- 타겟 대상: 대학생, 가족, 직장인\n\n" +
                            "출력 예시:\n\n" +
                            "공릉동 닭칼국수 임시 휴무 안내\n\n" +
                            "안녕하세요, 공릉동 닭칼국수입니다. 고객님들께 안내드립니다.\n" +
                            "저희 매장은 개인 일정으로 인해 임시 휴무합니다.\n" +
                            "이용에 참고 부탁드리며, 불편을 드려 죄송합니다.\n\n" +
                            "기간: 10월 23일 (수) 오전 9:00 ~ 10월 27일 (일) 오후 10:00\n\n" +
                            "매장 소식을 작성하는 데 필요한 모든 정보를 주어진 입력 형식에 맞춰 작성해주세요.";



            log.info(requestDto.getStartDate());
            log.info(requestDto.getEndDate());

            messages.add(new MessageRequestDto("system", prompt));
            messages.add(new MessageRequestDto("user", message));

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
            ResponseEntity<Map> response = restTemplate.postForEntity(NEWS_ENDPOINT, requestEntity, Map.class);
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