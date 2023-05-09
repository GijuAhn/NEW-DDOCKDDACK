package com.ddockddack.domain.ranking.repository;

import static com.ddockddack.domain.multigame.entity.QMultiGame.multiGame;

import com.ddockddack.domain.ranking.entity.Ranking;
import java.util.List;

public interface RankingRepositorySupport {
    List<Ranking> findByGameId(Long gameId);
}
