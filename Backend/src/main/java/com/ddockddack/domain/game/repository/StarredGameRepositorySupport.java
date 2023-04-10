package com.ddockddack.domain.game.repository;

import com.ddockddack.domain.game.response.StarredGameRes;
import java.util.List;

public interface StarredGameRepositorySupport {

    List<StarredGameRes> findAllStarredGame(Long memberId);

}
