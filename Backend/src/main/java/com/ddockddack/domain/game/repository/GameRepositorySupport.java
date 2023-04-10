package com.ddockddack.domain.game.repository;

import com.ddockddack.domain.game.response.GameDetailRes;
import com.ddockddack.domain.game.response.GameRes;
import com.ddockddack.global.util.PageCondition;
import java.util.List;
import org.springframework.data.domain.PageImpl;

public interface GameRepositorySupport {

    PageImpl<GameRes> findAllBySearch(Long memberId, PageCondition pageCondition);

    List<GameDetailRes> findGame(Long gameId);

    PageImpl<GameRes> findAllByMemberId(Long memberId, PageCondition pageCondition);

    List<Long> findGameIdsByMemberId(Long memberId);

}
