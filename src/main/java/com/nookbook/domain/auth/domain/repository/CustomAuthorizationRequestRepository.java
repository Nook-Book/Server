package com.nookbook.domain.auth.domain.repository;


import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nookbook.global.config.security.util.CustomCookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class CustomAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthorizationRequestRepository.class);

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    private static final int cookieExpireSeconds = 60 * 60;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CustomCookie.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> {
                    OAuth2AuthorizationRequest authRequest = CustomCookie.deserialize(cookie, OAuth2AuthorizationRequest.class);
                    logger.debug("Loaded OAuth2 Authorization Request: {}", authRequest);
                    return authRequest;
                })
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            logger.debug("Deleting OAuth2 Authorization Request cookies");
            CustomCookie.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CustomCookie.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
            return;
        }

        logger.debug("Saving OAuth2 Authorization Request: {}", authorizationRequest);
        CustomCookie.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CustomCookie.serialize(authorizationRequest), cookieExpireSeconds);
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            CustomCookie.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieExpireSeconds);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = this.loadAuthorizationRequest(request);
        logger.debug("Removing OAuth2 Authorization Request: {}", authorizationRequest);
        return authorizationRequest;
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Removing OAuth2 Authorization Request cookies");
        CustomCookie.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CustomCookie.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }
}
