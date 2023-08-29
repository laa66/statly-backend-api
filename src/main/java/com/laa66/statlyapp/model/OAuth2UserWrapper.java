package com.laa66.statlyapp.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.model.spotify.Image;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Value
public class OAuth2UserWrapper implements OAuth2User, Serializable {
    private final static ObjectMapper mapper = new ObjectMapper();
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

    public List<Image> getImages() {
        List<?> images = (List<?>) oAuth2User.getAttributes().get("images");
        return mapper.convertValue(images, new TypeReference<>(){});

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
