package com.nookbook.global.config.security.auth.company;

import com.nookbook.domain.user.domain.Provider;
import com.nookbook.global.config.security.auth.OAuth2UserInfo;

import java.util.Map;

public class Kakao extends OAuth2UserInfo {

    public Kakao(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {

        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {

        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {

        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {

        return (String) attributes.get("picture");
    }

    @Override
    public String getProvider(){
        return Provider.kakao.toString();
    }
}