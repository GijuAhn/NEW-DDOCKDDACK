package com.ddockddack.domain.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "MemberModifyNicknameRequest")
public class MemberModifyNameReq {
    @NotBlank(message = "닉네임을 입력해주세요.")
    String nickname;

    @Override
    public String toString() {
        return "MemberModifyReq{" +
            "nickname='" + nickname + '\'' +
            '}';
    }
}
