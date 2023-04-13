package com.ddockddack.domain.game.repository;

import static com.ddockddack.domain.game.entity.QGame.game;
import static com.ddockddack.domain.game.entity.QGameImage.gameImage;
import static com.ddockddack.domain.game.entity.QStarredGame.starredGame;
import static com.ddockddack.domain.member.entity.QMember.member;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.ExpressionUtils.as;
import static com.querydsl.jpa.JPAExpressions.select;

import com.ddockddack.domain.game.response.GameDetailRes;
import com.ddockddack.domain.game.response.GameRes;
import com.ddockddack.domain.game.response.QGameDetailRes;
import com.ddockddack.domain.game.response.QGameImageRes;
import com.ddockddack.domain.game.response.QGameRes;
import com.ddockddack.global.util.PageConditionReq;
import com.ddockddack.global.util.PeriodCondition;
import com.ddockddack.global.util.SearchCondition;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    // 검색 목록 조회
    @Override
    public PageImpl<GameRes> findAllBySearch(Long memberId, PageConditionReq pageCondition) {
        List<GameRes> list = jpaQueryFactory.select(
                new QGameRes(game.id.as("gameId"),
                    game.category.as("gameCategory").stringValue(),
                    game.title.as("gameTitle"),
                    game.description.as("gameDesc"),
                    game.member.id.as("memberId"),
                    game.member.nickname.as("creator"),
                    isStarred(memberId),
                    getStarredCnt(),
                    game.playCount.as("popularity"),
                    gameImage.imageUrl.min().as("thumbnail")
                ))
            .from(game)
            .innerJoin(game.member, member)
            .innerJoin(game.images, gameImage)
            .where(searchCond(pageCondition.getSearch(), pageCondition),
                periodCond(pageCondition.getPeriod()))
            .offset(pageCondition.getPageable().getOffset())
            .limit(pageCondition.getPageable().getPageSize())
            .groupBy(game.id,
                game.category,
                game.title,
                game.description,
                game.member.nickname,
                game.playCount)
            .orderBy(orderCond(pageCondition.getPageable()))
            .fetch();

        return new PageImpl<>(list, pageCondition.getPageable(),
            getTotalPageCount(memberId, pageCondition));
    }

    // 게임 상세 조회
    @Override
    public List<GameDetailRes> findGame(Long gameId) {
        return jpaQueryFactory
            .from(game)
            .innerJoin(gameImage).on(gameImage.game.id.eq(game.id))
            .where(game.id.eq(gameId))
            .transform(groupBy(game.id).list(new QGameDetailRes(
                game.id.as("gameId"),
                game.title.as("gameTitle"),
                game.description.as("gameDesc"),
                list(new QGameImageRes(
                    gameImage.id.as("gameImageId"),
                    gameImage.imageUrl.as("gameImageUrl"),
                    gameImage.description.as("gameImageDesc")))
            )));
    }

    // 내가 만든 게임 전체 조회
    @Override
    public PageImpl<GameRes> findAllByMemberId(Long memberId, PageConditionReq pageCondition) {

        List<GameRes> list = jpaQueryFactory.select(
                new QGameRes(game.id.as("gameId"),
                    game.category.as("gameCategory").stringValue(),
                    game.title.as("gameTitle"),
                    game.description.as("gameDesc"),
                    game.member.id.as("memberId"),
                    game.member.nickname.as("creator"),
                    isStarred(memberId),
                    getStarredCnt(),
                    game.playCount.as("popularity"),
                    gameImage.imageUrl.min().as("thumbnail")
                ))
            .from(game)
            .innerJoin(game.member, member)
            .innerJoin(game.images, gameImage)
            .where(member.id.eq(memberId),
                searchCond(pageCondition.getSearch(), pageCondition),
                periodCond(pageCondition.getPeriod()))
            .offset(pageCondition.getPageable().getOffset())
            .limit(pageCondition.getPageable().getPageSize())
            .groupBy(game.id,
                game.category,
                game.title,
                game.description,
                game.member.nickname,
                game.playCount)
            .orderBy(game.id.desc())
            .fetch();
        return new PageImpl<>(list, pageCondition.getPageable(),
            getTotalPageCount(memberId, pageCondition));
    }

    // 회원 탈퇴시 해당 회원이 만든 게임을 삭제 하기 위한 조회
    @Override
    public List<Long> findGameIdsByMemberId(Long memberId) {
        return jpaQueryFactory
            .select(game.id
            )
            .from(game)
            .where(game.member.id.eq(memberId))
            .fetch();
    }

    // 나만 쓸 거야

    private long getTotalPageCount(Long memberId, PageConditionReq pageCondition) {
        return jpaQueryFactory.selectDistinct(game.id)
            .from(game)
            .innerJoin(game.member, member)
            .innerJoin(game.images, gameImage)
            .where(searchCond(pageCondition.getSearch(), pageCondition),
                periodCond(pageCondition.getPeriod())).fetch().size();
    }

    // memberId 가 null 이면 isStarred 0 반환
    private Expression<Integer> isStarred(Long memberId) {
        if (memberId != null) {
            return as(
                select(starredGame.count().intValue())
                    .from(starredGame)
                    .where(starredGame.game.id.eq(game.id).and(
                        starredGame.member.id.eq(memberId))),
                "isStarred"
            );
        } else {
            return Expressions.as(Expressions.constant(0), "isStarred");
        }
    }

    // 게임의 즐겨찾기 수 구하기
    private Expression<Integer> getStarredCnt() {
        return as(
            select(starredGame.count().intValue())
                .from(starredGame)
                .where(starredGame.game.id.eq(game.id)),
            "starredCnt"
        );
    }


    private OrderSpecifier orderCond(Pageable pageable) {
        Sort.Order order = pageable.getSort().iterator().next();
        if (order.getProperty().equals("createdDate")) {
            return game.id.desc();
        } else {
            return game.playCount.desc();
        }
    }

    // 검색 조건
    private BooleanExpression searchCond(SearchCondition searchCondition,
        PageConditionReq pageCondition) {
        if (searchCondition == null) {
            return null;
        }

        if (SearchCondition.GAME.equals(searchCondition)) {
            return game.title.contains(pageCondition.getKeyword());
        }

        return game.member.nickname.contains(pageCondition.getKeyword());
    }

    // 기간 구하기
    private BooleanExpression periodCond(PeriodCondition periodCondition) {
        if (periodCondition == null || periodCondition.equals(PeriodCondition.ALL)) {
            return null;
        }

        return game.createdAt.goe(
            LocalDateTime.now().minusDays(periodCondition.getPeriod()));
    }
}
