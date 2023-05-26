package com.laa66.statlyapp.config;

import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        return getUserOrCreate(oAuth2User);
    }

    public OAuth2User getUserOrCreate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        String email;
        try {
            email = (String) oAuth2User.getAttributes().get("email");
            Optional<User> user = userService.findUserByEmail(email).or(() -> {
            userService.saveUser(new User(0, email, LocalDateTime.now()));
            return userService.findUserByEmail(email);
            });
            attributes.put("userId", user.orElseThrow().getId());
            return new DefaultOAuth2User(oAuth2User.getAuthorities(), Collections.unmodifiableMap(attributes), "display_name");
        } catch (NullPointerException | NoSuchElementException e) {
            return oAuth2User;
        }
    }


}
