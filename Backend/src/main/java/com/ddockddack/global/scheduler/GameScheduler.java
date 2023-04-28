package com.ddockddack.global.scheduler;

import com.ddockddack.domain.multigame.repository.MultiGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GameScheduler {

    private final MultiGameRepository multiGameRepository;

    @Scheduled(cron = "00 00 04 ? * *", zone = "Asia/Seoul")
    @Transactional
    public void syncStarredCount() {
        multiGameRepository.updateAll();
    }
}
