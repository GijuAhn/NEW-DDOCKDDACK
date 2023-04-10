package com.ddockddack.domain.bestcut.repository;

import static com.ddockddack.domain.bestcut.entity.QBestcut.bestcut;
import static com.ddockddack.domain.bestcut.entity.QBestcutLike.bestcutLike;
import static com.ddockddack.domain.member.entity.QMember.member;

import com.ddockddack.domain.bestcut.entity.Bestcut;
import com.ddockddack.domain.bestcut.response.BestcutRes;
import com.ddockddack.domain.bestcut.response.QBestcutRes;
import com.ddockddack.global.util.PageCondition;
import com.ddockddack.global.util.PeriodCondition;
import com.ddockddack.global.util.SearchCondition;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BestcutRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;


    public Long save(Bestcut bestcut) {
        em.persist(bestcut);
        return bestcut.getId();
    }

    public Optional<Bestcut> findById(Long bestcutId) {
        return Optional.ofNullable(em.find(Bestcut.class, bestcutId));
    }

    public void delete(Bestcut bestcut) {
        em.remove(bestcut);
    }


    public void deleteAllByIds(List<Long> bestcutIds) {
        em.createQuery("DELETE FROM Bestcut b WHERE b.id in :ids").setParameter("ids", bestcutIds)
            .executeUpdate();
    }

    public PageImpl<BestcutRes> findAllBySearch(Boolean my, Long loginMemberId,
        PageCondition pageCondition) {
        List<BestcutRes> resultList = jpaQueryFactory.select(
                new QBestcutRes(bestcut.id.as("bestcutId"), bestcut.title.as("bestcutImgTitle"),
                    bestcut.imageUrl.as("bestcutImgUrl"), bestcut.gameTitle,
                    bestcut.gameImageUrl, bestcut.gameImgDesc,
                    bestcut.createdAt.as("createdDate"), getLikeCnt(),
                    getIsLiked(loginMemberId), member.profile.as("profileImgUrl"),
                    member.nickname)).from(bestcut).join(bestcut.member, member)
            .where(myBestcut(my, loginMemberId), periodCond(pageCondition.getPeriodCondition()),
                searchCond(pageCondition.getSearchCondition(), pageCondition.getKeyword()))
            .orderBy(orderCond(pageCondition.getPageable()))
            .offset(pageCondition.getPageable().getOffset())
            .limit(pageCondition.getPageable().getPageSize())
            .fetch();

        return new PageImpl<>(resultList, pageCondition.getPageable(),
            getTotalPageCount(my, loginMemberId, pageCondition));
    }

    public List<Long> findByAllMemberId(Long memberId) {
        return em.createQuery("SELECT b.id FROM Bestcut b WHERE b.member.id = :id", Long.class)
            .setParameter("id", memberId)
            .getResultList();
    }

    public Optional<BestcutRes> findOne(Long loginMemberId, Long bestcutId) {
        List<BestcutRes> resultList = jpaQueryFactory.select(
                new QBestcutRes(bestcut.id.as("bestcutId"), bestcut.title.as("bestcutImgTitle"),
                    bestcut.imageUrl.as("bestcutImgUrl"), bestcut.gameTitle,
                    bestcut.gameImageUrl, bestcut.gameImgDesc,
                    bestcut.createdAt.as("createdDate"), getLikeCnt(),
                    getIsLiked(loginMemberId), member.profile.as("profileImgUrl"),
                    member.nickname)).from(bestcut).join(bestcut.member, member)
            .where(bestcut.id.eq(bestcutId))
            .fetch();

        return resultList.stream().findAny();
    }

    private static Expression<Integer> getLikeCnt() {
        return ExpressionUtils.as(
            JPAExpressions.select(bestcutLike.count().intValue()).from(bestcutLike)
                .where(bestcutLike.bestcut.id.eq(bestcut.id)), "popularity");
    }

    private static Expression<Integer> getIsLiked(Long loginMemberId) {
        return ExpressionUtils.as(
            JPAExpressions.select(bestcutLike.count().intValue()).from(bestcutLike)
                .where(bestcutLike.bestcut.id.eq(bestcut.id)
                    .and(bestcutLike.member.id.eq(loginMemberId))), "isLiked");
    }

    private long getTotalPageCount(Boolean my, Long loginMemberId, PageCondition pageCondition) {
        return jpaQueryFactory.select(Wildcard.count)
            .from(bestcut)
            .join(bestcut.member, member)
            .where(myBestcut(my, loginMemberId), periodCond(pageCondition.getPeriodCondition()),
                searchCond(pageCondition.getSearchCondition(), pageCondition.getKeyword()))
            .fetch()
            .get(0);
    }

    private BooleanExpression myBestcut(Boolean my, Long loginMemberId) {
        if (!my) {
            return null;
        }
        return bestcut.member.id.eq(loginMemberId);
    }


    private OrderSpecifier orderCond(Pageable pageable) {
        Order order = pageable.getSort().iterator().next();
        return Expressions.stringPath(order.getProperty()).desc();
    }

    private BooleanExpression periodCond(PeriodCondition periodCondition) {
        if (periodCondition == null) {
            return bestcut.createdAt.goe(LocalDateTime.now().minusDays(30));
        }

        if (periodCondition == PeriodCondition.ALL) {
            return null;
        }

        return bestcut.createdAt.goe(LocalDateTime.now().minusDays(periodCondition.getPeriod()));
    }

    private BooleanExpression searchCond(SearchCondition searchCondition, String keyword) {
        if (searchCondition == null) {
            return null;
        }

        if (searchCondition == SearchCondition.GAME) {
            return bestcut.gameTitle.contains(keyword.trim());
        }

        return bestcut.member.nickname.contains(keyword.trim());
    }

    private BooleanExpression bestcutEq(Long bestcutId) {
        if (bestcutId == null) {
            return null;
        }
        return bestcut.id.eq(bestcutId);
    }
}
