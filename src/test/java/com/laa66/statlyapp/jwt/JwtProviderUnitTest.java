package com.laa66.statlyapp.jwt;

import com.laa66.statlyapp.model.OAuth2UserWrapper;
import io.jsonwebtoken.io.Decoders;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderUnitTest {

    JwtProvider provider;
    ObjectMapper mapper = new ObjectMapper();
    OAuth2UserWrapper principal;

    @BeforeEach
    void setup() {
        provider = new JwtProvider("WTYmp3xWh8EfrHXcaHrlwFfZ11cvc3Tgx9xUiNuz7+E=");
        principal = new OAuth2UserWrapper(new DefaultOAuth2User(
                Collections.emptyList(), Map.of(
                        "userId", 1L, "display_name", "name"
        ), "display_name"
        ));
    }

    @Test
    void shouldCreateToken() throws IOException {
        String token = provider.createToken(principal);
        String[] split = token.split("\\.");
        String header = new String(Decoders.BASE64.decode(split[0]));
        String payload = new String(Decoders.BASE64.decode(split[1]));
        Map<?, ?> headerMap = mapper.readValue(header, Map.class);
        Map<?, ?> payloadMap = mapper.readValue(payload, Map.class);
        assertEquals(3, split.length);
        assertEquals("HS256", headerMap.get("alg"));
        assertEquals("statly", payloadMap.get("iss"));
        assertEquals("1", payloadMap.get("sub"));
        assertTrue(provider.validateToken(token));
    }

    @Test
    void shouldGetIdFromToken() {
        String token = provider.createToken(principal);
        Long userId = provider.getIdFromToken(token);
        assertEquals(1, userId);
    }

    @Test
    void shouldValidateToken() {
        String token = provider.createToken(principal);
        boolean valid = provider.validateToken(token);
        assertTrue(valid);
    }

    @Test
    void shouldValidateTokenInvalidToken() {
        String token = provider.createToken(principal);
        String invalidSignature = token.substring(0, token.length()-2) + RandomStringUtils.randomAlphanumeric(2);
        assertFalse(provider.validateToken(invalidSignature));

        String invalidToken = "219i3iji21.3km1k2";
        assertFalse(provider.validateToken(invalidToken));
    }
}