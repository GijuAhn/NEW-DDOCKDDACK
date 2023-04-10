package com.ddockddack.domain.report.service;

import com.ddockddack.domain.report.repository.ReportedBestcutRepository;
import com.ddockddack.domain.report.repository.ReportedGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportedGameRepository reportedGameRepository;
    private final ReportedBestcutRepository reportedBestcutRepository;


    /**
     * 신고된 게임 삭제
     *
     * @param reportId
     */
    public void removeReportedGame(Long reportId) {
        reportedGameRepository.deleteById(reportId);
    }

    /**
     * 신고된 베스트컷 삭제
     *
     * @param reportId
     */
    public void removeReportedBestcut(Long reportId) {
        reportedBestcutRepository.deleteById(reportId);
    }
}
