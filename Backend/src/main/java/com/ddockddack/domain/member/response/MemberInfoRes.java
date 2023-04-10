package com.ddockddack.domain.member.response;

import com.ddockddack.domain.member.entity.Role;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "memberInfoResponse")
public class MemberInfoRes {

    private Long memberId;
    private String email;
    private String nickname;
    private String profile;
    private Role role;


    @Builder
    @QueryProjection
    public MemberInfoRes(Long memberId, String email, String nickname, String profile, Role role) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.profile = profile;
        this.role = role;
    }

    public static MemberInfoRes of(Long memberId, String email, String nickname, String profile,
        Role role) {
        return MemberInfoRes.builder()
            .memberId(memberId)
            .email(email)
            .nickname(nickname)
            .profile(profile)
            .role(role)
            .build();
    }
}
