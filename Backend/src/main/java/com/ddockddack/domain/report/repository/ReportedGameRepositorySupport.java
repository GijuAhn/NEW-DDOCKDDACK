package com.ddockddack.domain.report.repository;

import com.ddockddack.domain.multigame.response.ReportedGameRes;
import java.util.List;

public interface ReportedGameRepositorySupport {

    List<ReportedGameRes> findAllReportedGame();
}
