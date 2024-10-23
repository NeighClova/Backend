package com.sogonsogon.neighclova.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.neighclova.domain.CustomOAuth2User;
import com.sogonsogon.neighclova.domain.User;
import com.sogonsogon.neighclova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        String oauthClientName = request.getClientRegistration().getClientName();

        User user = null;
        String userEmail = null;

        try {
            log.info(new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));

            if (oauthClientName.equals("naver")) {
                Map<String, String> responseMap = (Map<String, String>) oAuth2User.getAttributes().get("response");
                userEmail = responseMap.get("email");

                if (userEmail == null) {
                    throw new OAuth2AuthenticationException("Email not found in OAuth2 response");
                }

                user = new User(userEmail, "naver");
            }

            if (userRepo.existsByEmail(userEmail)){
                log.info("User with email {} signed in successfully.", userEmail);
            } else {
                userRepo.save(user);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new CustomOAuth2User(userEmail);
    }
}
