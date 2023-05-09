package com.ddockddack.domain.ranking.response;

import com.ddockddack.domain.report.entity.ReportType;
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
    private Long reportedMemberId;
    private String singleGameTitle;
    private String imageUrl;
    private ReportType reportType;

    @Builder
    public ReportedRankingRes(Long rankingId, Long reportedMemberId,
        String singleGameTitle, String imageUrl, ReportType reportType) {
        this.rankingId = rankingId;
        this.reportedMemberId = reportedMemberId;
        this.singleGameTitle = singleGameTitle;
        this.imageUrl = imageUrl;
        this.reportType = reportType;
    }

    public static ReportedRankingRes from(ReportedRanking reportedRanking) {
        return ReportedRankingRes.builder()
            .rankingId(reportedRanking.getRanking().getId())
            .reportedMemberId(reportedRanking.getRanking().getMember().getId())
            .singleGameTitle(reportedRanking.getRanking().getSingleGame().getTitle())
            .imageUrl(reportedRanking.getRanking().getImageUrl())
            .reportType(reportedRanking.getReportType())
            .build();

    }
}
