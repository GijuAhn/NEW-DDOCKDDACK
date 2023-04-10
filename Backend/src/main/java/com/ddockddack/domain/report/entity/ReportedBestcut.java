package com.ddockddack.domain.report.entity;

import com.ddockddack.domain.bestcut.entity.Bestcut;
import com.ddockddack.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportedBestcut {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reported_bestcut_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_member_id", foreignKey = @ForeignKey(name = "fk_reported_bestcut_report_member_id_idx"))
    private Member reportMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_member_id", foreignKey = @ForeignKey(name = "fk_reported_bestcut_reported_member_id_idx"))
    private Member reportedMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bestcut_id", foreignKey = @ForeignKey(name = "fk_reported_bestcut_bestcut_id_idx"))
    private Bestcut bestcut;

    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private ReportType reportType;

    @Builder
    public ReportedBestcut(Member reportMember, Member reportedMember, Bestcut bestcut, ReportType reportType) {
        this.reportMember = reportMember;
        this.reportedMember = reportedMember;
        this.bestcut = bestcut;
        this.reportType = reportType;
    }
}
