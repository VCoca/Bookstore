package com.example.demo.security;

import com.example.demo.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-koji-mora-imati-bar-256-bitova-duzine!!";

    private static final String EMAIL = "ivan@gmail.com";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", SECRET);
        ReflectionTestUtils.setField(jwtService, "EXPIRATION_TIME", 86400000L);
    }

    @Test
    @DisplayName("generated token contains email and role")
    void tokenContainsEmailAndRole() {
        String token = jwtService.generateToken(EMAIL, UserRole.ADMIN);

        assertThat(jwtService.extractEmail(token)).isEqualTo(EMAIL);
        assertThat(UserRole.valueOf(jwtService.extractAllClaims(token).get("role", String.class))).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("fresh token is valid")
    void freshTokenIsValid() {
        String token = jwtService.generateToken(EMAIL, UserRole.USER);

        assertThat(jwtService.isTokenValid(token, EMAIL)).isTrue();
    }

    @Test
    @DisplayName("expired token is not valid")
    void expiredTokenIsRejected() {
        ReflectionTestUtils.setField(jwtService, "EXPIRATION_TIME", -1L);
        String expired = jwtService.generateToken(EMAIL, UserRole.USER);

        assertThat(jwtService.isTokenValid(expired, EMAIL)).isFalse();
    }

    @Test
    @DisplayName("token with wrong signature is not valid")
    void tamperedTokenIsRejected() {
        String token = jwtService.generateToken(EMAIL, UserRole.USER);
        String[] parts = token.split("\\.");
        String fakePayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"ivan@gmail.com\",\"role\":\"ADMIN\"}".getBytes());

        String tampered = parts[0] + "." + fakePayload + "." + parts[2];

        assertThat(jwtService.isTokenValid(tampered, EMAIL)).isFalse();
    }

    @Test
    @DisplayName("garbage string is not valid token")
    void garbageIsRejected() {
        assertThat(jwtService.isTokenValid("ovo-nije-token", EMAIL)).isFalse();
    }

    @Test
    @DisplayName("token generated for other user is not valid")
    void tokenForDifferentUserIsRejected() {
        String token = jwtService.generateToken("marko@gmail.com", UserRole.USER);

        assertThat(jwtService.isTokenValid(token, EMAIL)).isFalse();
    }

}