package com.ddockddack.domain.report.entity;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.ranking.entity.Ranking;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportedRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reported_ranking_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reported_ranking_report_member_id_idx"))
    private Member reportMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reported_ranking_ranking_id_idx"))
    private Ranking ranking;

    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private ReportType reportType;

    @Builder
    public ReportedRanking(Member reportMember, Ranking ranking, ReportType reportType) {
        this.reportMember = reportMember;
        this.ranking = ranking;
        this.reportType = reportType;
    }
}
