package com.sogonsogon.neighclova.service;

import com.sogonsogon.neighclova.dto.request.CheckCertificationRequestDto;
import com.sogonsogon.neighclova.dto.request.EmailCertificationRequestDto;
import com.sogonsogon.neighclova.dto.request.EmailCheckRequestDto;
import com.sogonsogon.neighclova.dto.response.CheckCertificationResponseDto;
import com.sogonsogon.neighclova.dto.response.EmailCertificationResponseDto;
import com.sogonsogon.neighclova.dto.response.EmailCheckResponseDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<? super EmailCheckResponseDto> emailCheck(EmailCheckRequestDto dto);
    ResponseEntity<? super EmailCertificationResponseDto> emailCertification(EmailCertificationRequestDto dto);
    ResponseEntity<? super CheckCertificationResponseDto> checkCertification(CheckCertificationRequestDto dto);
}
