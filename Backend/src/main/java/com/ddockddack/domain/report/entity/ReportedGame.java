package com.ddockddack.domain.report.entity;

import com.ddockddack.domain.multigame.entity.MultiGame;
import com.ddockddack.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportedGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reported_game_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_member_id", foreignKey = @ForeignKey(name = "fk_reported_game_report_member_id_idx"))
    private Member reportMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_member_id", foreignKey = @ForeignKey(name = "fk_reported_game_reported_member_id_idx"))
    private Member reportedMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "multi_game_id", foreignKey = @ForeignKey(name = "fk_reported_game_game_id_idx"))
    private MultiGame multiGame;

    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private ReportType reportType;

    @Builder
    public ReportedGame(Member reportMember, Member reportedMember, MultiGame multiGame, ReportType reportType) {
        this.reportMember = reportMember;
        this.reportedMember = reportedMember;
        this.multiGame = multiGame;
        this.reportType = reportType;
    }
}
