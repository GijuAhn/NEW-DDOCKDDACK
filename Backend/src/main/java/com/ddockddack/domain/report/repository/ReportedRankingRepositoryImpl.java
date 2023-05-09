package com.ddockddack.domain.report.repository;

import static com.ddockddack.domain.member.entity.QMember.member;
import static com.ddockddack.domain.multigame.entity.QMultiGame.multiGame;
import static com.ddockddack.domain.ranking.entity.QRanking.ranking;
import static com.ddockddack.domain.report.entity.QReportedRanking.reportedRanking;
import static com.ddockddack.domain.singlegame.entity.QSingleGame.singleGame;

import com.ddockddack.domain.report.entity.ReportedRanking;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReportedRankingRepositoryImpl implements ReportedRankingRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean exist(Long memberId, Long rankingId) {
        final Integer fetchOne = jpaQueryFactory
            .selectOne()
            .from(reportedRanking)
            .where(reportedRanking.reportMember.id.eq(memberId),
                reportedRanking.ranking.id.eq(rankingId))
            .fetchFirst();
        return fetchOne != null;
    }

    @Override
    public List<ReportedRanking> findAllReportedRankings() {
        final List<ReportedRanking> fetch = jpaQueryFactory
            .selectFrom(reportedRanking)
            .innerJoin(reportedRanking.ranking, ranking).fetchJoin()
            .innerJoin(ranking.member, member).fetchJoin()
            .innerJoin(ranking.singleGame, singleGame).fetchJoin()
            .fetch();
        return fetch;
    }

    @Override
    public List<Long> findRankingIdsByRankingId(Long rankingId) {
        return jpaQueryFactory
            .select(reportedRanking.id
            )
            .from(reportedRanking)
            .innerJoin(reportedRanking.ranking, ranking)
            .where(reportedRanking.ranking.id.eq(rankingId))
            .fetch();
    }

}
