package com.laa66.statlyapp.config;

import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.entity.UserStats;
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

        try {
            String username = (String) attributes.get("display_name");
            String email = (String) oAuth2User.getAttributes().get("email");
            String spotifyUserId = (String) oAuth2User.getAttributes().get("id");
            String imageUrl = getImageUrl(attributes);

            UserDTO userDTO = Optional.ofNullable(userService.findUserByEmail(email))
                    .orElseGet(() -> userService.saveUser(new User(
                            0,
                            spotifyUserId,
                            username,
                            email,
                            imageUrl,
                            LocalDateTime.now(),
                            new UserStats())));
            attributes.put("userId", Long.parseLong(userDTO.getId()));
            return new DefaultOAuth2User(oAuth2User.getAuthorities(), Collections.unmodifiableMap(attributes), "display_name");
        } catch (NullPointerException | NoSuchElementException e) {
            return oAuth2User;
        }
    }

    private String getImageUrl(Map<String, Object> attributes) throws NoSuchElementException, NullPointerException {
        List<?> list = (List<?>) attributes.get("images");
        if (list.size() == 0) return "./account.png";
        Map<?, ?> images = (Map<?, ?>) list.get(0);
        return (String) images.get("url");
    }


}
