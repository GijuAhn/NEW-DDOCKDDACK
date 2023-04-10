package com.ddockddack.domain.report.entity;

import com.ddockddack.domain.game.entity.Game;
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
    @JoinColumn(name = "game_id", foreignKey = @ForeignKey(name = "fk_reported_game_game_id_idx"))
    private Game game;

    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private ReportType reportType;

    @Builder
    public ReportedGame(Member reportMember, Member reportedMember, Game game, ReportType reportType) {
        this.reportMember = reportMember;
        this.reportedMember = reportedMember;
        this.game = game;
        this.reportType = reportType;
    }
}
