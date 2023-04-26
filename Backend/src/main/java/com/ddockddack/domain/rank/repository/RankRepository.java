package com.ddockddack.domain.rank.repository;

import com.ddockddack.domain.rank.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankRepository extends JpaRepository<Ranking, Long>, RankRepositorySupport {
}
