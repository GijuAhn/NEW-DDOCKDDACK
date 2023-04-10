package com.ddockddack.domain.member.response;

import com.ddockddack.domain.member.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "memberJoinResponse")
public class MemberJoinRes {

    Long memberId;

    String email;

    String nickname;

    String profile;

    Role role;

    public static MemberJoinRes of(Integer statusCode, String message, MemberJoinRes memberInfo) {
        MemberJoinRes res = new MemberJoinRes();
        res.setEmail(memberInfo.getEmail());
        res.setNickname(memberInfo.getNickname());
//        res.setProfile(memberInfo.getProfile());
//        res.set(registerInfo.getGender());
        return res;
    }
}
