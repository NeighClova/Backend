package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.domain.Certification;
import com.sogonsogon.neighclova.dto.request.CheckCertificationRequestDto;
import com.sogonsogon.neighclova.dto.request.EmailCertificationRequestDto;
import com.sogonsogon.neighclova.dto.request.EmailCheckRequestDto;
import com.sogonsogon.neighclova.dto.response.CheckCertificationResponseDto;
import com.sogonsogon.neighclova.dto.response.EmailCertificationResponseDto;
import com.sogonsogon.neighclova.dto.response.EmailCheckResponseDto;
import com.sogonsogon.neighclova.dto.response.ResponseDto;
import com.sogonsogon.neighclova.provider.EmailProvider;
import com.sogonsogon.neighclova.repository.CertificationRepository;
import com.sogonsogon.neighclova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepo;
    private final EmailProvider emailProvider;
    private final CertificationRepository certificationRepo;

    @Override
    public ResponseEntity<? super EmailCheckResponseDto> emailCheck(EmailCheckRequestDto dto) {
        try{
            String email = dto.getEmail();
            boolean isExistEmail = userRepo.existsByEmail(email);
            if(isExistEmail) return EmailCheckResponseDto.duplicatedEmail();

        } catch(Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return EmailCheckResponseDto.success();
    }

    @Override
    public ResponseEntity<? super EmailCertificationResponseDto> emailCertification(EmailCertificationRequestDto dto) {
        try{
            String email = dto.getEmail();
            boolean isExistEmail = userRepo.existsByEmail(email);
            if(isExistEmail) return EmailCheckResponseDto.duplicatedEmail();

            String certificationNumber = generateValidationCode();

            boolean isSucceed = emailProvider.sendCertificationMail(email, certificationNumber);
            if(!isSucceed) return EmailCertificationResponseDto.mailSendFail();

            Certification certification = new Certification(email, certificationNumber);
            certificationRepo.save(certification);

        } catch(Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return EmailCertificationResponseDto.success();
    }

    @Override
    public ResponseEntity<? super CheckCertificationResponseDto> checkCertification(CheckCertificationRequestDto dto) {
        try{
            String email = dto.getEmail();
            String certificationNumber = dto.getCertificationNumber();

            Certification certification = certificationRepo.findByEmail(email);
            if (certification == null) return CheckCertificationResponseDto.certificationFail();

            boolean isMatched = certification.getEmail().equals(email) && certification.getCertificationNumber().equals(certificationNumber);
            if (!isMatched) return CheckCertificationResponseDto.certificationFail();

        } catch(Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return CheckCertificationResponseDto.success();
    }

    // 6자리 인증코드 생성
    private String generateValidationCode() {
        Random rand = new Random();
        int number = rand.nextInt(999999);

        return String.format("%06d", number);
    }
}
