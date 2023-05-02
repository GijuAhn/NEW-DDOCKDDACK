package com.ddockddack.domain.singlegame.repository;

import static com.ddockddack.domain.singlegame.entity.QSingleGame.singleGame;

import com.ddockddack.domain.singlegame.entity.SingleGame;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SingleGameRepositoryImpl implements SingleGameRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    public PageImpl<SingleGame> findSingleGames(String keyword, Pageable pageable) {
        List<SingleGame> singleGames = jpaQueryFactory.selectFrom(singleGame)
                .where(searchCond(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(singleGame.title.asc())
                .fetch();

        return new PageImpl<>(singleGames, pageable, getTotalPageCount(keyword));
    }

    private long getTotalPageCount(String keyword) {
        return jpaQueryFactory.select(singleGame.count())
                .from(singleGame)
                .where(searchCond(keyword)).fetchOne();
    }

    private BooleanExpression searchCond(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return singleGame.title.contains(keyword);
    }
}
