package com.nookbook.global.config.security.handler;


import com.nookbook.domain.auth.application.CustomTokenProviderService;
import com.nookbook.domain.auth.domain.Token;
import com.nookbook.domain.auth.domain.repository.CustomAuthorizationRequestRepository;
import com.nookbook.domain.auth.domain.repository.TokenRepository;
import com.nookbook.domain.auth.dto.response.TokenMapping;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.Provider;
import com.nookbook.domain.user.domain.User;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.OAuth2Config;
import com.nookbook.global.config.security.util.CustomCookie;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.nookbook.domain.auth.domain.repository.CustomAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Component
public class CustomSimpleUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private CustomTokenProviderService customTokenProviderService;

    @Autowired
    private OAuth2Config oAuth2Config;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CustomAuthorizationRequestRepository customAuthorizationRequestRepository;

    @Autowired
    private UserService userService; // 사용자 서비스 추가

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            return;
        }

        String targetUrl = determineTargetUrl(request, response, authentication);

        // 사용자 정보 저장
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String userName = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");

        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isEmpty()) {
            User user = User.builder()
                    .userName(userName)
                    .email(email)
                    .provider(Provider.google)
                    .providerId(providerId)
                    .build();
            userService.saveUser(user);
        } else {
            // 사용자 정보 업데이트 로직 추가 가능
        }

        TokenMapping token = customTokenProviderService.createToken(authentication);
        CustomCookie.addCookie(response, "Authorization", "Bearer_" + token.getAccessToken(), (int) oAuth2Config.getAuth().getAccessTokenExpirationMsec());
        CustomCookie.addCookie(response, "Refresh_Token", "Bearer_" + token.getRefreshToken(), (int) oAuth2Config.getAuth().getRefreshTokenExpirationMsec());

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CustomCookie.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);
        Token token = Token.builder()
                .userEmail(tokenMapping.getUserEmail())
                .refreshToken(tokenMapping.getRefreshToken())
                .build();
        tokenRepository.save(token);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", tokenMapping.getAccessToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        customAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return oAuth2Config.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
