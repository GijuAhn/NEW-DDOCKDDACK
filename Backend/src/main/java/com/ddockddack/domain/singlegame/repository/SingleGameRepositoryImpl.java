package com.ddockddack.domain.singlegame.repository;

import static com.ddockddack.domain.singlegame.entity.QSingleGame.singleGame;

import com.ddockddack.domain.multigame.request.paging.PageConditionReq;
import com.ddockddack.domain.singlegame.entity.SingleGame;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SingleGameRepositoryImpl implements SingleGameRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    public PageImpl<SingleGame> findSingleGames(PageConditionReq pageConditionReq) {
        List<SingleGame> singleGames = jpaQueryFactory.selectFrom(singleGame)
            .where(searchCond(pageConditionReq.getKeyword()))
            .offset(pageConditionReq.getPageable().getOffset())
            .limit(pageConditionReq.getPageable().getPageSize())
            .orderBy(singleGame.playCount.desc())
            .fetch();

        return new PageImpl<>(singleGames, pageConditionReq.getPageable(),
            getTotalPageCount(pageConditionReq.getKeyword()));
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
