package com.laa66.statlyapp.model;

import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@Value
public class OAuth2UserWrapper implements OAuth2User, Serializable {
    OAuth2User oAuth2User;

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getName();
    }

    @Override
    public <A> A getAttribute(String name) {
        return oAuth2User.getAttribute(name);
    }

    public String getCountry() {
        return (String) getAttributes().get("country");
    }

    public String getDisplayName() {
        return (String) getAttributes().get("display_name");
    }

    public String getId() {
        return (String) getAttributes().get("id");
    }

    public Long getUserId() {
        return (Long) getAttributes().get("userId");
    }

    public String getEmail() {
        return (String) getAttributes().get("email");
    }
}
