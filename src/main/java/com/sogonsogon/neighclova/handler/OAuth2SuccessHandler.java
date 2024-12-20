package com.sogonsogon.neighclova.handler;

import com.sogonsogon.neighclova.domain.CustomOAuth2User;
import com.sogonsogon.neighclova.provider.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String userEmail = oAuth2User.getName();
        String token = jwtProvider.createAccessToken(userEmail);
        String refresh = jwtProvider.createRefreshToken(userEmail);
        log.info(refresh);

        response.sendRedirect("http://localhost:3000/auth/oauth-response/"+ token + "/3600");



    }
}
