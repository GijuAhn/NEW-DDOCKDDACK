package com.ddockddack.domain.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Schema(description = "MemberBanRequest")
public class MemberBanReq {
    @NotNull(message = "아이디를 설정해주세요.") //admin ID인가 신고 Id인가, 신고 Id면 Req에 필요 없는 거 아닌가
    private Long reportId;

    @NotNull(message = "멤버 아이디를 설정해주세요.")
    private Long reportedMemberId;

    @NotNull(message = "Content Id를 설정해주세요.")
    private Long contentId;

    @NotNull(message = "정지 기간을 설정해주세요.") //신고 기간? 타입??
    private String type;

    @NotNull(message = "날짜를 설정해주세요.") //신고된 날짜?
    private int date;

}
