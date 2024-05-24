package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.domain.Introduce;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.dto.request.MessageRequestDto;
import com.sogonsogon.neighclova.dto.request.introduce.CreateIntroduceRequestDto;
import com.sogonsogon.neighclova.dto.request.introduce.IntroduceRequestDto;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.dto.response.introduce.GetIntroduceResponseDto;
import com.sogonsogon.neighclova.dto.response.introduce.IntroduceResponseDto;
import com.sogonsogon.neighclova.repository.IntroduceRepository;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class IntroduceService extends ResponseDto {

    @Value("${X_API_KEY}")
    private String X_API_KEY;

    @Value("${X_API_KEY_PRIMARY}")
    private String X_API_KEY_PRIMARY;

    @Value("${X_REQUEST_ID}")
    private String X_REQUEST_ID;

    private static final String ENDPOINT = "https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-003";

    private final PlaceRepository placeRepo;
    private final IntroduceRepository introduceRepo;

    @Transactional
    public ResponseEntity<? super IntroduceResponseDto> saveIntroduce(String email, IntroduceRequestDto requestDto) {
        try {
            Place place = placeRepo.findById(requestDto.getPlaceId()).orElseThrow(() -> new NoSuchElementException("Place not found"));

            // Place가 사용자 가게가 아니면 noPermission
            if (email.equals(place.getUserId().getEmail())) {
                Introduce introduce = requestDto.toEntity(place);
                introduceRepo.save(introduce);
            } else {
                return IntroduceResponseDto.noPermission();
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return IntroduceResponseDto.success();
    }

    @Transactional
    public ResponseEntity<? super GetIntroduceResponseDto> createIntroduce(Long placeId, CreateIntroduceRequestDto requestDto) {
        Place place;
        String content;
        try {

            place = placeRepo.findById(placeId).get();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-NCP-CLOVASTUDIO-API-KEY", X_API_KEY);
            headers.set("X-NCP-APIGW-API-KEY", X_API_KEY_PRIMARY);
            headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", X_REQUEST_ID);

            List<MessageRequestDto> messages = new ArrayList<>();

            String purpose = String.join(",", requestDto.getPurpose());
            String service = String.join(",", requestDto.getService());
            String mood = String.join(",", requestDto.getMood());
            String emphasizeContent = String.join(",", requestDto.getEmphasizeContent());

            String prompt = String.format(
                    "- 가게 명 : %s\n" +
                    "- 가게 업종 : %s\n" +
                    "- 방문 목적 : %s\n" +
                    "- 시설 및 서비스 : %s\n" +
                    "- 분위기 : %s\n" +
                    "- 강조 내용 : %s\n",
                    place.getPlaceName(), place.getCategory(), purpose,
                    service, mood, emphasizeContent
            );

            messages.add(new MessageRequestDto("user", prompt));

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
            content = (String) ((Map<String, Object>) result.get("message")).get("content");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetIntroduceResponseDto.success(content);
    }
}
