package com.ddockddack.domain.report.repository;

import com.ddockddack.domain.report.entity.ReportedRanking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportedRankingRepository extends JpaRepository<ReportedRanking, Long>,
    ReportedRankingRepositorySupport {

}
