package com.ddockddack.domain.report.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReportedRankingRepositoryImpl implements ReportedRankingRepositorySupport{

    private final JPAQueryFactory jpaQueryFactory;
}
