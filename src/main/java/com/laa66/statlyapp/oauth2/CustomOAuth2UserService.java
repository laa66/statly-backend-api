package com.laa66.statlyapp.oauth2;

import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.entity.UserStats;
import com.laa66.statlyapp.exception.UserAuthenticationException;
import com.laa66.statlyapp.model.Image;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return getUserOrCreate(new OAuth2UserWrapper(oAuth2User));
    }

    public OAuth2User getUserOrCreate(OAuth2UserWrapper oAuth2User) {
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        try {
            String email = oAuth2User.getEmail();
            UserDTO userDTO = Optional.ofNullable(userService.findUserByEmail(email))
                    .orElseGet(() -> userService.saveUser(new User(0,
                            oAuth2User.getId(),
                            oAuth2User.getDisplayName(),
                            email,
                            getImageUrl(attributes),
                            LocalDateTime.now(),
                            new UserStats())));
            attributes.put("userId", Long.parseLong(userDTO.getId()));
            return new OAuth2UserWrapper(new DefaultOAuth2User(oAuth2User.getAuthorities(),
                    Collections.unmodifiableMap(attributes),
                    "display_name"));
        } catch (NoSuchElementException | NullPointerException e) {
            throw new UserAuthenticationException("User cannot be properly authenticated");
        }
    }

    private String getImageUrl(Map<String, Object> attributes) throws NoSuchElementException, NullPointerException {
        return ((List<?>) attributes.get("images")).stream()
                .map(o -> (Map<?, ?>) o)
                .max(Comparator.comparing(o -> (Integer) o.get("height")))
                .map(map -> (String) map.get("url"))
                .orElse("./account.png");
    }


}
