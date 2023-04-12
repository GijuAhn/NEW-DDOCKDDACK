package com.ddockddack.global.oauth;

import com.ddockddack.domain.member.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "memberLoginPostResponse")
public class MemberDetail {
    private String accessToken;
    private Long id;
    private Role role;

    public MemberDetail(String accessToken,Long id, Role role) {
        this.accessToken = accessToken;
        this.id = id;
        this.role = role;
    }
}
