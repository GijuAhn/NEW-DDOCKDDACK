package com.ddockddack.domain.bestcut.repository;

import static com.ddockddack.domain.bestcut.entity.QBestcut.bestcut;
import static com.ddockddack.domain.bestcut.entity.QBestcutLike.bestcutLike;
import static com.ddockddack.domain.member.entity.QMember.member;
import static com.ddockddack.domain.report.entity.QReportedBestcut.reportedBestcut;

import com.ddockddack.domain.bestcut.response.BestcutRes;
import com.ddockddack.domain.bestcut.response.QBestcutRes;
import com.ddockddack.domain.bestcut.response.QReportedBestcutRes;
import com.ddockddack.domain.bestcut.response.ReportedBestcutRes;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BestcutRepositoryImpl implements BestcutRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    public PageImpl<BestcutRes> findAllBySearch(Boolean my, Long loginMemberId,
        PageCondition pageCondition) {
        List<BestcutRes> resultList = jpaQueryFactory.select(
                new QBestcutRes(bestcut.id.as("bestcutId"), bestcut.title.as("bestcutImgTitle"),
                    bestcut.imageUrl.as("bestcutImgUrl"), bestcut.gameTitle,
                    bestcut.gameImageUrl, bestcut.gameImgDesc,
                    bestcut.member.id.as("memberId"),
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

    public Optional<BestcutRes> findOne(Long loginMemberId, Long bestcutId) {
        List<BestcutRes> resultList = jpaQueryFactory.select(
                new QBestcutRes(bestcut.id.as("bestcutId"), bestcut.title.as("bestcutImgTitle"),
                    bestcut.imageUrl.as("bestcutImgUrl"), bestcut.gameTitle,
                    bestcut.gameImageUrl, bestcut.gameImgDesc,
                    bestcut.member.id.as("memberId"),
                    bestcut.createdAt.as("createdDate"), getLikeCnt(),
                    getIsLiked(loginMemberId), member.profile.as("profileImgUrl"),
                    member.nickname)).from(bestcut).join(bestcut.member, member)
            .where(bestcut.id.eq(bestcutId))
            .fetch();

        return resultList.stream().findAny();
    }

    public List<Long> findAllBestcutIdByMemberId(Long memberId) {
        return jpaQueryFactory
            .select(bestcut.id
            )
            .from(bestcut)
            .where(bestcut.member.id.eq(memberId))
            .fetch();
    }

    /**
     * @return
     */
    public List<ReportedBestcutRes> findAllReportedBestcut() {
        return jpaQueryFactory
            .select(new QReportedBestcutRes(
                reportedBestcut.id.as("reportId"),
                reportedBestcut.reportMember.id.as("reportMemberId"),
                reportedBestcut.reportedMember.id.as("reportedMemberId"),
                reportedBestcut.bestcut.id.as("gameId"),
                reportedBestcut.reportType.as("reason").stringValue(),
                reportedBestcut.bestcut.title.as("bestcutTitle"),
                reportedBestcut.reportMember.nickname.as("reportMemberNickname"),
                reportedBestcut.reportedMember.nickname.as("reportedMemberNickname")
            ))
            .from(reportedBestcut)
            .innerJoin(reportedBestcut.reportMember, member)
            .innerJoin(reportedBestcut.reportedMember, member)
            .orderBy(reportedBestcut.id.desc())
            .fetch();
    }


    private Expression<Integer> getLikeCnt() {
        return ExpressionUtils.as(
            JPAExpressions.select(bestcutLike.count().intValue()).from(bestcutLike)
                .where(bestcutLike.bestcut.id.eq(bestcut.id)), "popularity");
    }

    private Expression<Integer> getIsLiked(Long loginMemberId) {
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
}
