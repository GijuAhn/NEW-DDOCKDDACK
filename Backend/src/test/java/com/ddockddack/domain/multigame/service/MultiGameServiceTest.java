package com.ddockddack.domain.multigame.service;

import com.ddockddack.domain.multigame.response.ReportedGameRes;
import com.ddockddack.domain.report.repository.ReportedGameRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class MultiGameServiceTest {

    @InjectMocks
    MultiGameService multiGameService;

    @Mock
    ReportedGameRepository reportedGameRepository;

    Logger log;


    @Transactional
    @BeforeEach
    public void setUp() {
        log = (Logger) LoggerFactory.getLogger(ReportedGameRepository.class);
    }


    @Test
    void findAllGames() {
    }

    @Test
    void findGame() {
    }

    @Test
    void saveGame() {
    }

    @Test
    void modifyGame() {
    }

    @Test
    void removeGame() {
    }

    @Test
    void starredGame() {
    }

    @Test
    void unStarredGame() {
    }

    @Test
    void reportGame() {
    }

    @Test
    void findAllGamesByMemberId() {
    }

    @Test
    void findAllStarredGames() {
    }

    @Test
    @DisplayName("신고된 게임 리스트 조회")
    @Order(1)
    void findAllReportedGames() {
        // given
        List<ReportedGameRes> allReportedGames = new ArrayList<>();
        ReportedGameRes reportedGameRes = makeReportedGameRes();
        allReportedGames.add(reportedGameRes);
        doReturn(allReportedGames).when(reportedGameRepository).findAllReportedGame();

        // when
        List<ReportedGameRes> result  = multiGameService.findAllReportedGames();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getReportId()).isEqualTo(1L);
        assertThat(result.get(0).getGameTitle()).isEqualTo("테스트 게임 타이틀");

        // verify
        verify(reportedGameRepository, times(1)).findAllReportedGame();
    }

    private ReportedGameRes makeReportedGameRes() {
        ReportedGameRes reportedGameRes = new ReportedGameRes();
        reportedGameRes.setReportId(1L);
        reportedGameRes.setReportMemberId(2L);
        reportedGameRes.setReportedMemberId(3L);
        reportedGameRes.setGameId(4L);
        reportedGameRes.setReason("테스트 사유입니다.");
        reportedGameRes.setGameTitle("테스트 게임 타이틀");
        reportedGameRes.setReportMemberNickname("테스트 신고자 닉네임");
        reportedGameRes.setReportedMemberNickname("테스트 피신고자 닉네임");

        return reportedGameRes;
    }
}