package com.ddockddack.domain.ranking.repository;

import static com.ddockddack.domain.member.entity.QMember.member;
import static com.ddockddack.domain.ranking.entity.QRanking.ranking;

import com.ddockddack.domain.ranking.entity.Ranking;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Ranking> findByGameId(Long gameId) {
        List<Long> ids = jpaQueryFactory.select(ranking.id)
            .from(ranking)
            .where(ranking.singleGame.id.eq(gameId))
            .orderBy(ranking.score.desc())
            .limit(20)
            .fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        return jpaQueryFactory
            .selectFrom(ranking)
            .where(ranking.id.in(ids))
            .leftJoin(ranking.member, member)
            .fetchJoin()
            .orderBy(ranking.score.desc())
            .fetch();
    }
}
