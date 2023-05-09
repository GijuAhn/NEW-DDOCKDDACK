package com.ddockddack.domain.report.repository;

import com.ddockddack.domain.report.entity.ReportedRanking;
import java.util.List;

public interface ReportedRankingRepositorySupport {

    boolean exist(Long memberId, Long rankingId);

    List<ReportedRanking> findAllReportedRankings();
}
