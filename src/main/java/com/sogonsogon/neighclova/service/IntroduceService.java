package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.domain.Introduce;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.dto.object.IntroduceListItem;
import com.sogonsogon.neighclova.dto.request.MessageRequestDto;
import com.sogonsogon.neighclova.dto.request.RequestOverrideDto;
import com.sogonsogon.neighclova.dto.request.introduce.CreateIntroduceRequestDto;
import com.sogonsogon.neighclova.dto.request.introduce.IntroduceRequestDto;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.dto.response.introduce.Get3IntroduceResponseDto;
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

    @Value("${X_NCP_CLOVASTUDIO_INTRODUCE_REQUEST_ID}")
    private String X_NCP_CLOVASTUDIO_INTRODUCE_REQUEST_ID;

    @Value("${X_NCP_CLOVASTUDIO_API_KEY}")
    private String X_NCP_CLOVASTUDIO_API_KEY;

    @Value("${X_NCP_APIGW_API_KEY}")
    private String X_NCP_APIGW_API_KEY;

    @Value("${INTRODUCE_ENDPOINT}")
    private String INTRODUCE_ENDPOINT;


    private final PlaceRepository placeRepo;
    private final IntroduceRepository introduceRepo;

    @Transactional
    public ResponseEntity<? super Get3IntroduceResponseDto> get3Introduce(Long placeId, String email) {
        List<IntroduceListItem> introduceListItems = new ArrayList<>();
        Place place;
        try {
            place = placeRepo.findById(placeId).orElse(null);
            if (place != null & email.equals(place.getUserId().getEmail())) {
                List<Introduce> introduces = introduceRepo.findTop3ByPlace(place);

                for (Introduce introduce : introduces)
                    introduceListItems.add(IntroduceListItem.of(introduce));
            }
            else {
                return IntroduceResponseDto.noPermission();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        Get3IntroduceResponseDto responseDto = new Get3IntroduceResponseDto(introduceListItems, place);
        return responseDto.success(introduceListItems, place);
    }

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
            headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", X_NCP_CLOVASTUDIO_INTRODUCE_REQUEST_ID);
            headers.set("X-NCP-CLOVASTUDIO-API-KEY", X_NCP_CLOVASTUDIO_API_KEY);
            headers.set("X-NCP-APIGW-API-KEY", X_NCP_APIGW_API_KEY);

            List<MessageRequestDto> messages = new ArrayList<>();

            String purpose = String.join(",", requestDto.getPurpose());
            String service = String.join(",", requestDto.getService());
            String mood = String.join(",", requestDto.getMood());
            String emphasizeContent = String.join(",", requestDto.getEmphasizeContent());

            String message = String.format(
                    "- 매장명: %s\n" +
                    "- 매장 업종: %s\n" +
                    "- 매장 방문 목적 키워드: %s\n" +
                    "- 매장 시설과 서비스 키워드: %s\n" +
                    "- 매장 분위기 키워드: %s\n" +
                    "- 강조하고 싶은 내용: %s\n",
                    place.getPlaceName(), place.getCategory(), purpose,
                    service, mood, emphasizeContent
            );

            String prompt = "- 키워드를 기반으로 매장의 소개 글을 생성한다.\n- 강조하고 싶은 내용이 없는 경우 임의로 작성하지 않는다. \n\n예시)\n- 매장명: 공릉동 닭칼국수\n- 매장 업종: 한식\n- 매장 방문 목적 키워드: 회식, 가족모임\n- 매장 시설과 서비스 키워드: 단체석, 좌식, 입식, 주차공간\n- 매장 분위기 키워드: 혼밥, 혼술, 편한좌석\n- 강조하고 싶은 내용: 50년 전통 닭칼국수\n\n\n공릉동 닭칼국수는 한식 전문점으로, 맛있는 닭칼국수를 즐길 수 있는 곳입니다. 회식이나 가족모임에 적합한 편안한 분위기를 제공하며, 단체석, 좌식 및 입식 좌석 등 다양한 좌석 옵션과 주차 공간을 갖추고 있습니다. 혼밥이나 혼술을 즐기기에도 좋으며, 누구나 편안하게 이용할 수 있는 매장입니다.";

            messages.add(new MessageRequestDto("system", prompt));
            messages.add(new MessageRequestDto("user", message));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messages", messages);
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
            ResponseEntity<Map> response = restTemplate.postForEntity(INTRODUCE_ENDPOINT, requestEntity, Map.class);
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
