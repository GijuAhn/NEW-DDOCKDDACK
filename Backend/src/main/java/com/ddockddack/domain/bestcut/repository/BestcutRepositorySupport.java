package com.ddockddack.domain.bestcut.repository;

import com.ddockddack.domain.bestcut.response.BestcutRes;
import com.ddockddack.domain.bestcut.response.ReportedBestcutRes;
import com.ddockddack.global.util.PageCondition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageImpl;


public interface BestcutRepositorySupport {
    PageImpl<BestcutRes> findAllBySearch(Boolean my, Long loginMemberId, PageCondition pageCondition);

    Optional<BestcutRes> findOne(Long loginMemberId, Long bestcutId);

    List<Long> findAllBestcutIdByMemberId(Long memberId);

    List<ReportedBestcutRes> findAllReportedBestcut();
}
