package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.domain.Certification;
import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.domain.User;
import com.sogonsogon.neighclova.dto.request.auth.*;
import com.sogonsogon.neighclova.dto.response.*;
import com.sogonsogon.neighclova.dto.response.auth.*;
import com.sogonsogon.neighclova.provider.EmailProvider;
import com.sogonsogon.neighclova.provider.JwtProvider;
import com.sogonsogon.neighclova.repository.CertificationRepository;
import com.sogonsogon.neighclova.repository.PlaceRepository;
import com.sogonsogon.neighclova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PlaceRepository placeRepo;
    private final JwtProvider jwtProvider;
    private final EmailProvider emailProvider;
    private final CertificationRepository certificationRepo;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public ResponseEntity<? super EmailCheckResponseDto> emailCheck(EmailCheckRequestDto dto) {
        try {
            String email = dto.getEmail();
            boolean isExistEmail = userRepo.existsByEmail(email);
            if (isExistEmail)
                return EmailCheckResponseDto.duplicatedEmail();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return EmailCheckResponseDto.success();
    }

    @Override
    public ResponseEntity<? super EmailCertificationResponseDto> emailCertification(EmailCertificationRequestDto dto) {
        try {
            String email = dto.getEmail();
            boolean isExistEmail = userRepo.existsByEmail(email);
            if (isExistEmail)
                return EmailCheckResponseDto.duplicatedEmail();

            String certificationNumber = generateValidationCode();

            boolean isSucceed = emailProvider.sendCertificationMail(email, certificationNumber);
            if (!isSucceed)
                return EmailCertificationResponseDto.mailSendFail();

            Certification certification = new Certification(email, certificationNumber);
            certificationRepo.save(certification);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return EmailCertificationResponseDto.success(dto.getEmail());
    }

    @Override
    public ResponseEntity<? super CheckCertificationResponseDto> checkCertification(CheckCertificationRequestDto dto) {
        try {
            String email = dto.getEmail();
            String certificationNumber = dto.getCertificationNumber();

            Certification certification = certificationRepo.findByEmail(email);
            if (certification == null)
                return CheckCertificationResponseDto.certificationFail();

            boolean isMatched = certification.getEmail().equals(email)
                    && certification.getCertificationNumber().equals(certificationNumber);
            if (!isMatched)
                return CheckCertificationResponseDto.certificationFail();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return CheckCertificationResponseDto.success();
    }

    @Override
    public ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto dto) {
        try {
            String email = dto.getEmail();
            String certificationNumber = dto.getCertificationNumber();

            boolean isExistEmail = userRepo.existsByEmail(email);
            if (isExistEmail)
                return SignUpResponseDto.duplicatedEmail();

            Certification certification = certificationRepo.findByEmail(email);
            boolean isMatched = certification.getEmail().equals(email)
                    && certification.getCertificationNumber().equals(certificationNumber);
            if (!isMatched)
                return SignUpResponseDto.certificationFail();

            // password encoding
            String password = dto.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            dto.setPassword(encodedPassword);

            User user = new User(dto);
            userRepo.save(user);

            certificationRepo.deleteByEmail(email);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return SignUpResponseDto.success();
    }

    @Override
    public ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto) {

        String accessToken = null;
        String refreshToken = null;

        try {
            String uid = dto.getUid();
            User user = userRepo.findByUid(uid);
            if (user == null || !user.isStatus())
                return SignInResponseDto.signInFail();

            String password = dto.getPassword();
            String encodedPassword = user.getPassword();
            boolean isMatched = passwordEncoder.matches(password, encodedPassword);
            if (!isMatched)
                return SignInResponseDto.signInFail();

            accessToken = jwtProvider.createAccessToken(user.getEmail());
            refreshToken = jwtProvider.createRefreshToken(user.getEmail());
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return SignInResponseDto.success(accessToken, refreshToken);
    }

    @Override
    public ResponseEntity<? super TokenResponseDto> reissue(String refreshToken) {
        String accessToken = null;
        String newRefreshToken = null;
        List<String> tokens = new ArrayList<>();

        try {
            tokens = jwtProvider.reissue(refreshToken);
            if (tokens == null) {
                log.info("tokens get null");
                return ResponseDto.expiredToken();
            }

            accessToken = tokens.get(0);
            newRefreshToken = tokens.get(1);
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.validationFail();
        }

        return TokenResponseDto.success(accessToken, newRefreshToken);
    }

    @Override
    public ResponseEntity<ResponseDto> patchPassword(PatchPasswordRequestDto dto, String email) {
        try {
            User user = userRepo.findByEmail(email);
            if (user == null || !user.isStatus())
                return ResponseDto.notExistUser();

            // newPassword encoding
            String newPassword = dto.getNewPassword();
            String newEncodedPassword = passwordEncoder.encode(newPassword);

            user.patchPassword(newEncodedPassword);
            userRepo.save(user);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto());
    }

    // 회원 탈퇴
    @Override
    public ResponseEntity<? super DeleteUserResponseDto> deleteUser(String email) {
        try {

            User user = userRepo.findByEmail(email);
            // 사용자가 존재하지 않거나, 탈퇴 상태인 경우
            if (user == null || !user.isStatus())
                return DeleteUserResponseDto.notExistUser();

            user.patchStatus();
            userRepo.save(user);

            List<Place> placeList = placeRepo.findAllByUserId(user);
            for (Place place : placeList) {
                placeRepo.delete(place);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return DeleteUserResponseDto.success();
    }

    // 현재 비밀번호 확인
    @Override
    public ResponseEntity<ResponseDto> checkPassword(CheckPasswordRequestDto dto, String email) {
        try {
            User user = userRepo.findByEmail(email);
            if (user == null || !user.isStatus())
                return ResponseDto.notExistUser();

            // 이전 비밀번호와 현재 user의 비밀번호가 일치한지
            String password = dto.getPassword();
            String encodedPassword = user.getPassword();
            boolean isMatched = passwordEncoder.matches(password, encodedPassword);
            if (!isMatched)
                return ResponseDto.noPermission();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto());
    }

    // 소셜 로그인 여부 확인
    @Override
    public ResponseEntity<ResponseDto> checkSocial(String email) {
        try {
            User user = userRepo.findByEmail(email);
            if (user == null || !user.isStatus())
                return ResponseDto.notExistUser();

            String type = user.getType();
            if (!type.equals("app"))
                return ResponseDto.noPermission();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto());
    }

    // 아이디 중복 확인
    @Override
    public ResponseEntity<ResponseDto> checkId(CheckIdRequestDto dto) {
        try {
            String uid = dto.getUid();
            boolean isExistUid = userRepo.existsByUid(uid);
            if (isExistUid)
                return ResponseDto.duplicatedId();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto());
    }

    // 이메일로 아이디 찾기
    @Override
    public ResponseEntity<? super SendUidResponseDto> sendUidByEmail(EmailCheckRequestDto dto) {
        try {
            // 이메일이 유효한지 확인
            String email = dto.getEmail();
            User user = userRepo.findByEmail(email);

            // 회원이 존재하고 탈퇴하지 않았다면
            if (user != null && user.isStatus()) {
                // 유효하다면 해당 이메일로 id 전송
                boolean isSucceed = emailProvider.sendUidMail(email, user.getUid());
                if (!isSucceed)
                    return SendUidResponseDto.mailSendFail();

                return SendUidResponseDto.success();
            } else
                // 유효하지 않다면 notExistEmail error 전송
                return SendUidResponseDto.notExistedEmail();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    // [비로그인-비밀번호 수정] 아이디 입력 시 사용자 이메일로 코드 전송
    @Override
    public ResponseEntity<? super EmailCertificationResponseDto> uidCertification(uidCertificationRequestDto dto) {
        String email = null;
        try {
            String uid = dto.getUid();
            User user = userRepo.findByUid(uid);
            if (user == null || !user.isStatus())
                return EmailCertificationResponseDto.notExistUser();

            String certificationNumber = generateValidationCode();
            email = user.getEmail();
            boolean isSucceed = emailProvider.sendCertificationMail(email, certificationNumber);
            if (!isSucceed)
                return EmailCertificationResponseDto.mailSendFail();

            Certification certification = new Certification(email, certificationNumber);
            certificationRepo.save(certification);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return EmailCertificationResponseDto.success(email);
    }

    // 6자리 인증코드 생성
    private String generateValidationCode() {
        Random rand = new Random();
        int number = rand.nextInt(999999);

        return String.format("%06d", number);
    }
}
