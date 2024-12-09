package com.sogonsogon.neighclova.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sogonsogon.neighclova.KISA_SEED_CBC;
import com.sogonsogon.neighclova.domain.Feedback;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.domain.User;
import com.sogonsogon.neighclova.dto.object.PlaceListItem;
import com.sogonsogon.neighclova.dto.request.place.InstagramRequestDto;
import com.sogonsogon.neighclova.dto.request.place.PlaceRequestDto;
import com.sogonsogon.neighclova.dto.request.place.ProfileImgRequestDto;
import com.sogonsogon.neighclova.dto.request.place.UploadInstagramRequestDto;
import com.sogonsogon.neighclova.dto.response.place.GetAllPlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.place.GetInstagramResponseDto;
import com.sogonsogon.neighclova.dto.response.place.GetPlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.place.PlaceResponseDto;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.repository.FeedbackRepository;
import com.sogonsogon.neighclova.repository.PlaceRepository;
import com.sogonsogon.neighclova.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaceService {

    private final UserRepository userRepo;
    private final PlaceRepository placeRepo;
    private final FeedbackRepository feedbackRepo;

    @Value("${application.bucket.name}")
    private String bucketName;

    @Value("${PBSZ_USER_KEY}")
    private String PBSZ_USER_KEY;

    @Value("${PBSZ_IV}")
    private String PBSZ_IV;

    @Autowired
    private AmazonS3 s3Client;

    private byte[] pbszUserKey;
    private byte[] pbszIV;

    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final String ENDPOINT = "http://127.0.0.1:8000/instagram/upload";

    @PostConstruct
    public void init() {
        this.pbszUserKey = PBSZ_USER_KEY.getBytes(UTF_8);
        this.pbszIV = PBSZ_IV.getBytes(UTF_8);
    }

    @Transactional
    public ResponseEntity<? super PlaceResponseDto> savePlace(String email, PlaceRequestDto dto) {
        try {
            User user = userRepo.findByEmail(email);
            if (user != null) {
                Place place = dto.toEntity(user);

                // place 저장 후 place 반환
                Place savedPlace = placeRepo.save(place);

                int remainder = (int) ((savedPlace.getPlaceId()) % 7);
                String dayOfWeek = getDayOfWeek(remainder);

                Feedback feedback = Feedback.builder()
                        .placeId(savedPlace)
                        .viewDate(dayOfWeek)
                        .createdAt(LocalDateTime.now())
                        .build();

                feedbackRepo.save(feedback);
            } else {
                return PlaceResponseDto.notExistUser();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PlaceResponseDto.success();
    }

    @Transactional
    public ResponseEntity<? super PlaceResponseDto> patchPlace(Long placeId, String email, PlaceRequestDto dto) {
        try {
            Optional<Place> placeOptional = placeRepo.findById(placeId);
            if (!placeOptional.isPresent()) return PlaceResponseDto.notExistedPlace();

            Place place = placeOptional.get();
            User user = userRepo.findByEmail(email);
            Long ownerId = place.getUserId().getId();

            if (!ownerId.equals(user.getId())) return PlaceResponseDto.noPermission();

            place.patchPlace(dto, user);
            placeRepo.save(place);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PlaceResponseDto.success();
    }

    @Transactional
    public ResponseEntity<? super PlaceResponseDto> patchProfileImg(Long placeId, String email, MultipartFile file) {
        try {
            Optional<Place> placeOptional = placeRepo.findById(placeId);
            if (!placeOptional.isPresent()) return PlaceResponseDto.notExistedPlace();

            Place place = placeOptional.get();
            User user = userRepo.findByEmail(email);
            Long ownerId = place.getUserId().getId();

            if (!ownerId.equals(user.getId())) return PlaceResponseDto.noPermission();

            File fileObj = convertMultiPartFileToFile(file);
            String fileName =  Long.toString(placeId);

            // key가 존재하면 기존 파일은 삭제
            if ("".equals(fileName) == false && fileName != null) {
                boolean isExistObject = s3Client.doesObjectExist(bucketName, fileName);

                if (isExistObject == true) {
                    s3Client.deleteObject(bucketName, fileName);
                    log.info("중복 제거");
                }
            }

            s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            log.info("접근 완료");
            fileObj.delete();
            log.info("저장 완료");

            // S3에 저장된 이미지 호출하기
            URL url = s3Client.getUrl("neighclova-s3", Long.toString(placeId));
            String urltext = ""+url;

            place.patchProfileImg(urltext);
            placeRepo.save(place);
            log.info(urltext);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PlaceResponseDto.success();
    }

    @Transactional
    public ResponseEntity<? super GetAllPlaceResponseDto> getAllPlace(String email) {
        List<PlaceListItem> placeListItems = new ArrayList<>();
        try {
            User user = userRepo.findByEmail(email);
            List<Place> places = placeRepo.findAllByUserId(user);

            for (Place place : places)
                placeListItems.add(PlaceListItem.of(place));

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        GetAllPlaceResponseDto responseDto = new GetAllPlaceResponseDto(placeListItems);
        return responseDto.success(placeListItems);
    }

    @Transactional
    public ResponseEntity<? super GetPlaceResponseDto> getPlace(Long placeId) {
        Place place;
        try {
            if (placeRepo.existsById(placeId)) {
                place = placeRepo.findById(placeId).get();
            } else {
                return GetPlaceResponseDto.noExistPlace();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetPlaceResponseDto.success(place);
    }

    @Transactional
    public ResponseEntity<? super PlaceResponseDto> saveInstagram(String email, InstagramRequestDto dto) {
        try {
            Optional<Place> placeOptional = placeRepo.findById(dto.getPlaceId());
            if (!placeOptional.isPresent()) return PlaceResponseDto.notExistedPlace();

            Place place = placeOptional.get();
            User user = userRepo.findByEmail(email);
            Long ownerId = place.getUserId().getId();

            if (!ownerId.equals(user.getId())) return PlaceResponseDto.noPermission();

            // password encoding
            String password = dto.getPassword();
            String encodedPassword = encrypt(password);
            dto.setPassword(encodedPassword);

            place.patchInstagram(dto);
            placeRepo.save(place);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PlaceResponseDto.success();
    }

    @Transactional
    public ResponseEntity<? super PlaceResponseDto> patchInstagram(String email, InstagramRequestDto dto) {
        try {
            Optional<Place> placeOptional = placeRepo.findById(dto.getPlaceId());
            if (!placeOptional.isPresent()) return PlaceResponseDto.notExistedPlace();

            Place place = placeOptional.get();
            User user = userRepo.findByEmail(email);
            Long ownerId = place.getUserId().getId();

            if (!ownerId.equals(user.getId())) return PlaceResponseDto.noPermission();

            // password encoding
            String password = dto.getPassword();
            String encodedPassword = encrypt(password);
            dto.setPassword(encodedPassword);

            place.patchInstagram(dto);
            placeRepo.save(place);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PlaceResponseDto.success();
    }

    @Transactional
    public ResponseEntity<? super GetInstagramResponseDto> getInstagram(String email, Long placeId) {
        String id;
        String password;

        try {
            Optional<Place> placeOptional = placeRepo.findById(placeId);
            if (!placeOptional.isPresent()) return GetInstagramResponseDto.noExistPlace();

            Place place = placeOptional.get();
            User user = userRepo.findByEmail(email);
            Long ownerId = place.getUserId().getId();

            if (!ownerId.equals(user.getId())) return PlaceResponseDto.noPermission();

            id = place.getInstagramId();
            // password decoding
            password = decrypt(place.getInstagramPw());


        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetInstagramResponseDto.success(placeId, id, password);
    }

    @Transactional
    public ResponseEntity<? super PlaceResponseDto> uploadInstagram(String email, UploadInstagramRequestDto dto, MultipartFile file) {
        try {
            Optional<Place> placeOptional = placeRepo.findById(dto.getPlaceId());
            if (!placeOptional.isPresent()) return PlaceResponseDto.notExistedPlace();

            Place place = placeOptional.get();
            User user = userRepo.findByEmail(email);
            Long ownerId = place.getUserId().getId();

            if (!ownerId.equals(user.getId())) return PlaceResponseDto.noPermission();

            String password = decrypt(place.getInstagramPw());

            sendMultipartRequest(place.getInstagramId(), password, dto.getContent(), file);
            log.info("successfully uploaded");

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PlaceResponseDto.success();
    }

    public static String getDayOfWeek(int remainder) {
        Map<Integer, String> dayMap = new HashMap<>();
        dayMap.put(0, "일요일");
        dayMap.put(1, "월요일");
        dayMap.put(2, "화요일");
        dayMap.put(3, "수요일");
        dayMap.put(4, "목요일");
        dayMap.put(5, "금요일");
        dayMap.put(6, "토요일");

        return dayMap.getOrDefault(remainder, "Invalid");
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

    public String encrypt(String rawMessage) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] message = rawMessage.getBytes(UTF_8);
        byte[] encryptedMessage = KISA_SEED_CBC.SEED_CBC_Encrypt(pbszUserKey, pbszIV, message, 0, message.length);

        return new String(encoder.encode(encryptedMessage), UTF_8);
    }

    public String decrypt(String encryptedMessage) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] message = decoder.decode(encryptedMessage);
        byte[] decryptedMessage = KISA_SEED_CBC.SEED_CBC_Decrypt(pbszUserKey, pbszIV, message, 0, message.length);

        return new String(decryptedMessage, UTF_8);
    }

    public void sendMultipartRequest(String instagramId, String instagramPw, String content, MultipartFile multipartFile) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("instagramId", instagramId);
        body.add("instagramPw", instagramPw);
        body.add("content", content);

        // Wrap the MultipartFile as a ByteArrayResource for the file upload
        ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
            @Override
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };

        body.add("file", fileResource);

        // HttpEntity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // POST Request
        ResponseEntity<String> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST, requestEntity, String.class);

        // Response handling
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Response: " + response.getBody());
        } else {
            System.out.println("Request failed with status code: " + response.getStatusCode());
        }
    }

}
