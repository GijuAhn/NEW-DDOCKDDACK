package com.ddockddack.domain.multigame.repository;

import static com.ddockddack.domain.multigame.entity.QGameImage.gameImage;
import static com.ddockddack.domain.multigame.entity.QMultiGame.multiGame;
import static com.ddockddack.domain.multigame.entity.QStarredGame.starredGame;
import static com.ddockddack.domain.member.entity.QMember.member;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.ExpressionUtils.as;
import static com.querydsl.jpa.JPAExpressions.select;

import com.ddockddack.domain.multigame.response.MultiGameDetailRes;
import com.ddockddack.domain.multigame.response.MultiGameRes;
import com.ddockddack.domain.multigame.response.QMultiGameDetailRes;
import com.ddockddack.domain.multigame.response.QGameImageRes;
import com.ddockddack.domain.multigame.response.QMultiGameRes;
import com.ddockddack.domain.multigame.request.paging.PageConditionReq;
import com.ddockddack.domain.multigame.request.paging.PeriodCondition;
import com.ddockddack.domain.multigame.request.paging.SearchCondition;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
@RequiredArgsConstructor
public class MultiGameRepositoryImpl implements MultiGameRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    // 검색 목록 조회
    @Override
    public PageImpl<MultiGameRes> findAllBySearch(Long memberId, PageConditionReq pageCondition) {
        List<Long> ids = jpaQueryFactory.select(multiGame.id)
                .from(multiGame)
                .join(multiGame.member, member)
                .where(searchCond(pageCondition.getSearch(), pageCondition),
                        periodCond(pageCondition.getPeriod()))
                .offset(pageCondition.getPageable().getOffset())
                .limit(pageCondition.getPageable().getPageSize())
                .orderBy(orderCond(pageCondition.getPageable()))
                .fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new PageImpl<>(new ArrayList<>(), pageCondition.getPageable(),
                    getTotalPageCount(pageCondition));
        }

        List<MultiGameRes> list = jpaQueryFactory.select(
                        new QMultiGameRes(multiGame.id.as("gameId"),
                                multiGame.title.as("gameTitle"),
                                multiGame.description.as("gameDesc"),
                                multiGame.member.id.as("memberId"),
                                multiGame.member.nickname.as("creator"),
                                isStarred(memberId),
                                multiGame.starredCnt.as("starredCnt"),
                                multiGame.playCount.as("popularity"),
                                multiGame.thumbnail.as("thumbnail")
                        ))
                .from(multiGame)
                .where(multiGame.id.in(ids))
                .orderBy(orderCond(pageCondition.getPageable()))
                .fetch();

        return new PageImpl<>(list, pageCondition.getPageable(),
            getTotalPageCount(pageCondition));
    }

    // 게임 상세 조회
    @Override
    public List<MultiGameDetailRes> findGame(Long gameId) {
        return jpaQueryFactory
            .from(multiGame)
            .innerJoin(gameImage).on(gameImage.multiGame.id.eq(multiGame.id))
            .where(multiGame.id.eq(gameId))
            .transform(groupBy(multiGame.id).list(new QMultiGameDetailRes(
                    multiGame.id.as("gameId"),
                    multiGame.title.as("gameTitle"),
                    multiGame.description.as("gameDesc"),
                list(new QGameImageRes(
                    gameImage.id.as("gameImageId"),
                    gameImage.imageUrl.as("gameImageUrl"),
                    gameImage.description.as("gameImageDesc")))
            )));
    }

    // 내가 만든 게임 전체 조회
    @Override
    public PageImpl<MultiGameRes> findAllByMemberId(Long memberId, PageConditionReq pageCondition) {

        List<MultiGameRes> list = jpaQueryFactory.select(
                new QMultiGameRes(multiGame.id.as("gameId"),
                        multiGame.title.as("gameTitle"),
                        multiGame.description.as("gameDesc"),
                        multiGame.member.id.as("memberId"),
                        multiGame.member.nickname.as("creator"),
                    isStarred(memberId),
                        multiGame.starredCnt.as("starredCnt"),
                        multiGame.playCount.as("popularity"),
                        multiGame.thumbnail.as("thumbnail")
                ))
            .from(multiGame)
            .innerJoin(multiGame.member, member)
            .where(member.id.eq(memberId))
            .offset(pageCondition.getPageable().getOffset())
            .limit(pageCondition.getPageable().getPageSize())
            .orderBy(multiGame.id.desc())
            .fetch();
        return new PageImpl<>(list, pageCondition.getPageable(),
            getTotalPageCount(memberId));
    }

    // 회원 탈퇴시 해당 회원이 만든 게임을 삭제 하기 위한 조회
    @Override
    public List<Long> findGameIdsByMemberId(Long memberId) {
        return jpaQueryFactory
            .select(multiGame.id
            )
            .from(multiGame)
            .where(multiGame.member.id.eq(memberId))
            .fetch();
    }

    // 나만 쓸 거야

    private long getTotalPageCount(PageConditionReq pageCondition) {
        return jpaQueryFactory.select(multiGame.count())
            .from(multiGame)
            .innerJoin(multiGame.member, member)
            .where(searchCond(pageCondition.getSearch(), pageCondition),
                periodCond(pageCondition.getPeriod())).fetchOne();
    }

    private long getTotalPageCount(Long memberId) {
        return jpaQueryFactory.select(multiGame.id)
                .from(multiGame)
                .innerJoin(multiGame.member, member)
                .where(member.id.eq(memberId)).fetch().size();
    }

    // memberId 가 null 이면 isStarred 0 반환
    private Expression<Integer> isStarred(Long memberId) {
        if (memberId != null) {
            return as(
                select(starredGame.count().intValue())
                    .from(starredGame)
                    .where(starredGame.multiGame.id.eq(multiGame.id).and(
                        starredGame.member.id.eq(memberId))),
                "isStarred"
            );
        } else {
            return Expressions.as(Expressions.constant(0), "isStarred");
        }
    }

    // 게임의 즐겨찾기 수 구하기
    @Override
    public List<Tuple> getStarredCnt() {
        return jpaQueryFactory
            .select(starredGame.multiGame.id,
                starredGame.count())
            .from(starredGame)
            .where(starredGame.multiGame.id.eq(multiGame.id))
            .groupBy(starredGame.multiGame.id)
            .fetch();
    }


    private OrderSpecifier orderCond(Pageable pageable) {
        Sort.Order order = pageable.getSort().iterator().next();
        if (order.getProperty().equals("createdDate")) {
            return multiGame.id.desc();
        } else {
            return multiGame.playCount.desc();
        }
    }

    // 검색 조건
    private BooleanExpression searchCond(SearchCondition searchCondition,
        PageConditionReq pageCondition) {
        if("".equals(pageCondition.getKeyword())) {
            return null;
        }

        if (SearchCondition.GAME.equals(searchCondition)) {
            return multiGame.title.contains(pageCondition.getKeyword());
        }

        return multiGame.member.nickname.contains(pageCondition.getKeyword());
    }

    // 기간 구하기
    private BooleanExpression periodCond(PeriodCondition periodCondition) {
        if (periodCondition == null || periodCondition.equals(PeriodCondition.ALL)) {
            return null;
        }

        return multiGame.createdAt.goe(
            LocalDateTime.now().minusDays(periodCondition.getPeriod()));
    }
}
