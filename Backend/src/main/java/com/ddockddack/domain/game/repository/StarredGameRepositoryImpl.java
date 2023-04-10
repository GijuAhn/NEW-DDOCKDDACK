package com.ddockddack.domain.game.repository;

import static com.ddockddack.domain.game.entity.QGame.game;
import static com.ddockddack.domain.game.entity.QGameImage.gameImage;
import static com.ddockddack.domain.game.entity.QStarredGame.starredGame;
import static com.ddockddack.domain.member.entity.QMember.member;
import static com.querydsl.core.types.ExpressionUtils.as;
import static com.querydsl.jpa.JPAExpressions.select;

import com.ddockddack.domain.game.response.QStarredGameRes;
import com.ddockddack.domain.game.response.StarredGameRes;
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
                starredGame.game.id.as("gameId"),
                starredGame.game.category.as("gameCategory").stringValue(),
                starredGame.game.title.as("gameTitle"),
                starredGame.game.description.as("gameDesc"),
                starredGame.game.member.nickname.as("creator"),
                isStarred(memberId),
                getStarredCnt(),
                starredGame.game.playCount.as("playCnt"),
                gameImage.imageUrl.min().as("thumbnail")
            ))
            .from(starredGame)
            .innerJoin(starredGame.game, game)
            .innerJoin(starredGame.game.member, member)
            .innerJoin(starredGame.game.images, gameImage)
            .where(starredGame.member.id.eq(memberId))
            .groupBy(
                starredGame.id,
                starredGame.game.id,
                starredGame.game.category,
                starredGame.game.title,
                starredGame.game.member.nickname,
                starredGame.game.createdAt,
                starredGame.game.playCount)
            .orderBy(starredGame.id.desc())
            .fetch();
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
}
