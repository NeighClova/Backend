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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static java.util.Objects.isNull;

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

        return EmailCertificationResponseDto.success();
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
            log.info(password);
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

        String token = null;

        try {
            String email = dto.getEmail();
            User user = userRepo.findByEmail(email);
            if (user == null || !user.isStatus())
                return SignInResponseDto.signInFail();

            String password = dto.getPassword();
            String encodedPassword = user.getPassword();
            boolean isMatched = passwordEncoder.matches(password, encodedPassword);
            if (!isMatched)
                return SignInResponseDto.signInFail();

            token = jwtProvider.create(email);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return SignInResponseDto.success(token);
    }

    @Override
    public ResponseEntity<? super PatchPasswordResponseDto> patchPassword(PatchPasswordRequestDto dto, String email) {
        try {
            User user = userRepo.findByEmail(email);
            if (user == null || !user.isStatus())
                return PatchPasswordResponseDto.notExistUser();

            // 이전 비밀번호와 현재 user의 비밀번호가 일치한지
            String oldPassword = dto.getOldPassword();
            String encodedPassword = user.getPassword();
            boolean isMatched = passwordEncoder.matches(oldPassword, encodedPassword);
            if (!isMatched)
                return PatchPasswordResponseDto.noPermission();

            // newPassword encoding
            String newPassword = dto.getNewPassword();
            String newEncodedPassword = passwordEncoder.encode(newPassword);

            user.patchPassword(newEncodedPassword);
            userRepo.save(user);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PatchPasswordResponseDto.success();
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

    // 6자리 인증코드 생성
    private String generateValidationCode() {
        Random rand = new Random();
        int number = rand.nextInt(999999);

        return String.format("%06d", number);
    }
}
