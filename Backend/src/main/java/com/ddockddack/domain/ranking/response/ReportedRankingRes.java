package com.ddockddack.domain.ranking.response;

import com.ddockddack.domain.report.entity.ReportedRanking;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ReportedRankingRes {
    private Long rankingId;
    private Long reportMemberId;
    private Long reportedMemberId;
    private String singleGameTitle;
    private String imageUrl;

    @Builder
    public ReportedRankingRes(Long rankingId, Long reportMemberId, Long reportedMemberId,
        String singleGameTitle, String imageUrl) {
        this.rankingId = rankingId;
        this.reportMemberId = reportMemberId;
        this.reportedMemberId = reportedMemberId;
        this.singleGameTitle = singleGameTitle;
        this.imageUrl = imageUrl;
    }

    public static ReportedRankingRes from(ReportedRanking reportedRanking) {
        return ReportedRankingRes.builder()
            .rankingId(reportedRanking.getRanking().getId())
            .reportMemberId(reportedRanking.getReportMember().getId())
            .reportedMemberId(reportedRanking.getRanking().getMember().getId())
            .singleGameTitle(reportedRanking.getRanking().getSingleGame().getTitle())
            .imageUrl(reportedRanking.getRanking().getImageUrl())
            .build();

    }
}
