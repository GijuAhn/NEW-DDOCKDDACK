package com.ddockddack.domain.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Schema(description = "MemberLoginRequest")
public class MemberModifyReq {
    @NotBlank(message = "닉네임을 입력해주세요.")
    String nickname;
    @NotBlank(message = "프로필 사진을 설정해주세요.")
    String profile;
}
