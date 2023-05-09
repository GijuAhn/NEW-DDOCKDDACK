package com.ddockddack.domain.ranking.repository;

import com.ddockddack.domain.ranking.entity.Ranking;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long>, RankingRepositorySupport {
    Optional<Ranking> findBySingleGameIdAndMemberId(Long gameId, Long memberId);

}
