package com.ddockddack.domain.multigame.repository;

import static com.ddockddack.domain.multigame.entity.QGameImage.gameImage;
import static com.ddockddack.domain.multigame.entity.QMultiGame.multiGame;
import static com.ddockddack.domain.multigame.entity.QStarredGame.starredGame;
import static com.ddockddack.domain.member.entity.QMember.member;
import static com.querydsl.core.types.ExpressionUtils.as;
import static com.querydsl.jpa.JPAExpressions.select;

import com.ddockddack.domain.multigame.response.QStarredGameRes;
import com.ddockddack.domain.multigame.response.StarredGameRes;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StarredGameRepositoryImpl implements StarredGameRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    // 즐겨찾기 한 게임 목록 조회
    @Override
    public List<StarredGameRes> findAllStarredGame(Long memberId) {
        return jpaQueryFactory
            .select(new QStarredGameRes(
                starredGame.multiGame.id.as("gameId"),
                starredGame.multiGame.title.as("gameTitle"),
                starredGame.multiGame.description.as("gameDesc"),
                starredGame.multiGame.member.nickname.as("creator"),
                isStarred(memberId),
                getStarredCnt(),
                starredGame.multiGame.playCount.as("playCnt"),
                starredGame.multiGame.thumbnail.as("thumbnail")
            ))
            .from(starredGame)
            .innerJoin(starredGame.multiGame, multiGame)
            .innerJoin(starredGame.multiGame.member, member)
            .where(starredGame.member.id.eq(memberId))
            .orderBy(starredGame.id.desc())
            .fetch();
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
    private Expression<Integer> getStarredCnt() {
        return as(
            select(starredGame.count().intValue())
                .from(starredGame)
                .where(starredGame.multiGame.id.eq(multiGame.id)),
            "starredCnt"
        );
    }
}
