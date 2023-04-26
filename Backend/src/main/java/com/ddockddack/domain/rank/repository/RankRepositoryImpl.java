package com.ddockddack.domain.rank.repository;

import com.ddockddack.domain.rank.entity.Ranking;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class RankRepositoryImpl implements RankRepositorySupport{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<Ranking> findAllByGameId(Long gameId) {
//        jpaQueryFactory.select(rank.id).from(rank)
        return null;
    }
}
