package com.ddockddack.domain.multigame.repository;

import com.ddockddack.domain.multigame.response.StarredGameRes;
import java.util.List;

public interface StarredGameRepositorySupport {

    List<StarredGameRes> findAllStarredGame(Long memberId);

}
