package com.nookbook.global.config.security.auth;

import com.nookbook.domain.user.domain.Provider;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.auth.company.Google;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(Provider.google.toString())) {
            return new Google(attributes);
        } else {
            DefaultAssert.isAuthentication("해당 oauth2 기능은 지원하지 않습니다.");
        }
        return null;
    }
}