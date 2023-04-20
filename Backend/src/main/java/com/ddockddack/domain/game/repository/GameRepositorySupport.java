package com.ddockddack.domain.game.repository;

import com.ddockddack.domain.game.response.GameDetailRes;
import com.ddockddack.domain.game.response.GameRes;
import com.ddockddack.global.util.PageConditionReq;
import com.querydsl.core.Tuple;
import java.util.List;
import org.springframework.data.domain.PageImpl;

public interface GameRepositorySupport {

    PageImpl<GameRes> findAllBySearch(Long memberId, PageConditionReq pageCondition);

    List<GameDetailRes> findGame(Long gameId);

    PageImpl<GameRes> findAllByMemberId(Long memberId, PageConditionReq pageCondition);

    List<Long> findGameIdsByMemberId(Long memberId);

    List<Tuple> getStarredCnt();

}
