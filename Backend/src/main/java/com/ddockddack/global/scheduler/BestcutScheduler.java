package com.ddockddack.global.scheduler;

import com.ddockddack.domain.bestcut.repository.BestcutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BestcutScheduler {

    private final BestcutRepository bestcutRepository;

    @Scheduled(cron = "00 00 04 ? * *", zone = "Asia/Seoul")
    @Transactional
    public void syncLikeCount() {
        bestcutRepository.syncLikeCount();
    }
}
