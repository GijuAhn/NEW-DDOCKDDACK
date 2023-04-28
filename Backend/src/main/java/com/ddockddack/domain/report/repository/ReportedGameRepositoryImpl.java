package com.ddockddack.domain.report.repository;

import static com.ddockddack.domain.member.entity.QMember.member;
import static com.ddockddack.domain.report.entity.QReportedGame.reportedGame;

import com.ddockddack.domain.multigame.response.QReportedGameRes;
import com.ddockddack.domain.multigame.response.ReportedGameRes;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReportedGameRepositoryImpl implements  ReportedGameRepositorySupport{

    private final JPAQueryFactory jpaQueryFactory;

    // 신고된 게임 목록 조회
    @Override
    public List<ReportedGameRes> findAllReportedGame() {
        return jpaQueryFactory
            .select(new QReportedGameRes(
                reportedGame.id.as("reportId"),
                reportedGame.reportMember.id.as("reportMemberId"),
                reportedGame.reportedMember.id.as("reportedMemberId"),
                reportedGame.multiGame.id.as("gameId"),
                reportedGame.reportType.as("reason").stringValue(),
                reportedGame.multiGame.title.as("gameTitle"),
                reportedGame.reportMember.nickname.as("reportMemberNickname"),
                reportedGame.reportedMember.nickname.as("reportedMemberNickname")
            ))
            .from(reportedGame)
            .innerJoin(reportedGame.reportMember, member)
            .innerJoin(reportedGame.reportedMember, member)
            .orderBy(reportedGame.id.desc())
            .fetch();
    }

}
