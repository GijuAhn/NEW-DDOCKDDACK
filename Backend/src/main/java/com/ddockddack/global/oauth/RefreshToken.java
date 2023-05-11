package com.ddockddack.global.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshToken {

    private Long uid;
    private String refreshToken;

    public RefreshToken(Long uid, String refreshToken) {
        this.uid = uid;
        this.refreshToken = refreshToken;
    }
}
