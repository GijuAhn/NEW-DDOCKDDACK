package com.ddockddack.domain.game.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ReportedGameRes {
    private Long reportId;
    private Long reportMemberId;
    private Long reportedMemberId;
    private Long gameId;
    private String reason;
    private String gameTitle;
    private String reportMemberNickname;
    private String reportedMemberNickname;

    @QueryProjection
    public ReportedGameRes(Long reportId, Long reportMemberId, Long reportedMemberId, Long gameId, String reason, String gameTitle, String reportMemberNickname, String reportedMemberNickname) {
        this.reportId = reportId;
        this.reportMemberId = reportMemberId;
        this.reportedMemberId = reportedMemberId;
        this.gameId = gameId;
        this.reason = reason;
        this.gameTitle = gameTitle;
        this.reportMemberNickname = reportMemberNickname;
        this.reportedMemberNickname = reportedMemberNickname;
    }
}
