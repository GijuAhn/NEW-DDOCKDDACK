package com.ddockddack.domain.multigame.repository;

import com.ddockddack.domain.multigame.response.MultiGameDetailRes;
import com.ddockddack.domain.multigame.response.MultiGameRes;
import com.ddockddack.domain.multigame.request.paging.PageConditionReq;
import com.querydsl.core.Tuple;
import java.util.List;
import org.springframework.data.domain.PageImpl;

public interface MultiGameRepositorySupport {

    PageImpl<MultiGameRes> findAllBySearch(Long memberId, PageConditionReq pageCondition);

    List<MultiGameDetailRes> findGame(Long gameId);

    PageImpl<MultiGameRes> findAllByMemberId(Long memberId, PageConditionReq pageCondition);

    List<Long> findGameIdsByMemberId(Long memberId);

    List<Tuple> getStarredCnt();

}
