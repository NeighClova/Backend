package com.sogonsogon.neighclova.provider;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    // 회원가입 인증 코드 전송
    private final String SUBJECT = "[NeighClova] 인증메일입니다.";

    public boolean sendCertificationMail(String email, String certificationNumber) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getCertificationMessage(certificationNumber);

            messageHelper.setTo(email);
            messageHelper.setSubject(SUBJECT);
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (Exception exception){
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    private String getCertificationMessage (String certificationNumber) {
        String certificationMessage = "";
        certificationMessage += "<h1 style='text-align: center;'>[NeighClova] 인증메일</h1>";
        certificationMessage += "<h3 style='text-align: center;'>인증코드 : <strong style='font-size: 32px; letter-spacing: 8px;'>" + certificationNumber + "</strong></h3>";
        return certificationMessage;

    }

    // 아이디 전송
    private final String ID_SUBJECT = "[NeighClova] 아이디 확인 메일입니다.";

    public boolean sendUidMail(String email, String uid) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getUidMessage(uid);

            messageHelper.setTo(email);
            messageHelper.setSubject(ID_SUBJECT);
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (Exception exception){
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    private String getUidMessage(String uid) {
        String certificationMessage = "";
        certificationMessage += "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ccc; max-width: 600px; margin: auto;'>";
        certificationMessage += "<h1 style='text-align: center; color: #000;'>[NeighClova] 아이디 확인 메일</h1>";
        certificationMessage += "<p style='font-size: 16px; margin-bottom: 20px;'>안녕하세요.</p>";
        certificationMessage += "<p style='font-size: 16px; margin-bottom: 20px;'>회원님께서 조회하신 아이디는 다음과 같습니다.</p>";
        certificationMessage += "<p style='font-size: 16px; font-weight: bold;'>아이디: " + uid + "</p>";
        certificationMessage += "<p style='margin-top: 30px; font-size: 14px; color: #888;'>* 본 메일은 개인정보 관련 보안 메일이므로 확인 즉시 삭제하는 것이 안전합니다.</p>";
        certificationMessage += "<p style='font-size: 16px;'>NeighClova 드림</p>";
        certificationMessage += "</div>";

        return certificationMessage;
    }

}
