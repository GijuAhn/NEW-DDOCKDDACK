package com.ddockddack.domain.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "memberLoginPostResponse")
public class MemberAccessRes {
    private String accessToken;
    private Long id;

    public MemberAccessRes(String accessToken,Long id) {
        this.accessToken = accessToken;
        this.id = id;
    }
}
