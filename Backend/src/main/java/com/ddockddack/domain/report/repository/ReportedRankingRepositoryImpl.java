package com.ddockddack.domain.report.repository;

import static com.ddockddack.domain.report.entity.QReportedRanking.reportedRanking;

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
            .innerJoin(reportedRanking.ranking).fetchJoin()
            .innerJoin(reportedRanking.ranking.member).fetchJoin()
            .innerJoin(reportedRanking.reportMember).fetchJoin()
            .innerJoin(reportedRanking.ranking.singleGame).fetchJoin()
            .fetch();
        return fetch;
    }
}
