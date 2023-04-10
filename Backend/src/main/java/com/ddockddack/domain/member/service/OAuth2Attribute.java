package com.ddockddack.domain.member.service;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
class OAuth2Attribute {

    private Map<String, Object> attributes;
    private String attributeKey;
    private String email;
    private String nickname;
    private String profile;

    static OAuth2Attribute of(String provider, String attributeKey,
        Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return ofGoogle(attributeKey, attributes);
            case "kakao":
                return ofKakao(attributeKey, attributes);
            default:
                throw new RuntimeException();
        }
    }

    private static OAuth2Attribute ofGoogle(String attributeKey,
        Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
            .nickname((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .profile((String) attributes.get("profile"))
            .attributes(attributes)
            .attributeKey(attributeKey)
            .build();
    }

    private static OAuth2Attribute ofKakao(String attributeKey,
        Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2Attribute.builder()
            .nickname((String) kakaoProfile.get("nickname"))
            .email((String) kakaoAccount.get("email"))
            .profile((String) kakaoProfile.get("profile_image_url"))
            .attributes(kakaoAccount)
            .attributeKey(attributeKey)
            .build();
    }


    Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", attributeKey);
        map.put("nickname", nickname);
        map.put("email", email);
        map.put("profile", profile);

        return map;
    }
}