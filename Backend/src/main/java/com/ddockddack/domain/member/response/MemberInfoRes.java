package com.ddockddack.domain.member.response;

import com.ddockddack.domain.member.entity.Role;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "memberInfoResponse")
public class MemberInfoRes {
    private Long memberId;
    private String email;
    private String nickname;
    private String profile;
    private Role role;

    @QueryProjection
    public MemberInfoRes(Long memberId, String email, String nickname, String profile, Role role) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.profile = profile;
        this.role = role;
    }

    public MemberInfoRes(String nickname, String profile) {
        this.nickname = nickname;
        this.profile = profile;
    }
}
