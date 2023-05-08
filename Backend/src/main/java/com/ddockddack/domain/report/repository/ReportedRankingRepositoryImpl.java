package com.ddockddack.domain.report.repository;

import static com.ddockddack.domain.report.entity.QReportedRanking.reportedRanking;

import com.querydsl.jpa.impl.JPAQueryFactory;
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
}
