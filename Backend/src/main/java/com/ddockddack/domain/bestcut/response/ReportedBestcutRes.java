package com.ddockddack.domain.bestcut.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReportedBestcutRes {
    private Long reportId;
    private Long reportMemberId;
    private Long reportedMemberId;
    private Long bestcutId;
    private String reason;
    private String bestcutTitle;
    private String reportMemberNickname;
    private String reportedMemberNickname;

    @QueryProjection
    public ReportedBestcutRes(Long reportId, Long reportMemberId, Long reportedMemberId, Long bestcutId, String reason, String bestcutTitle, String reportMemberNickname, String reportedMemberNickname) {
        this.reportId = reportId;
        this.reportMemberId = reportMemberId;
        this.reportedMemberId = reportedMemberId;
        this.bestcutId = bestcutId;
        this.reason = reason;
        this.bestcutTitle = bestcutTitle;
        this.reportMemberNickname = reportMemberNickname;
        this.reportedMemberNickname = reportedMemberNickname;
    }
}
