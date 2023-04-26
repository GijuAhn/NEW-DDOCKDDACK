package com.ddockddack.domain.rank.repository;

import com.ddockddack.domain.rank.entity.Ranking;
import java.util.List;

public interface RankRepositorySupport {

    List<Ranking> findAllByGameId(Long gameId);
}
