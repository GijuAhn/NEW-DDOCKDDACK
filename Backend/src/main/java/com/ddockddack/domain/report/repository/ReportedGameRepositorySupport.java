package com.ddockddack.domain.report.repository;

import com.ddockddack.domain.game.response.ReportedGameRes;
import java.util.List;

public interface ReportedGameRepositorySupport {

    List<ReportedGameRes> findAllReportedGame();
}
