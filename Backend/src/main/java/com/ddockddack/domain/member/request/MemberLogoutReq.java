package com.ddockddack.domain.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Schema(description = "MemberLogoutRequest")
public class MemberLogoutReq {
    @NotBlank(message = "id를 입력해주세요.")
    Long id;
}
