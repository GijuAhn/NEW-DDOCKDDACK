package com.ddockddack.domain.ranking.repository;

import com.ddockddack.domain.ranking.entity.Ranking;
import java.util.List;

public interface RankingRepositorySupport {
    List<Ranking> findByGameId(Long gameId);
}
