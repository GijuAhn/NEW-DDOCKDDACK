package com.ddockddack.domain.multigame.service;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.entity.Role;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.multigame.entity.MultiGame;
import com.ddockddack.domain.multigame.repository.MultiGameRepository;
import com.ddockddack.domain.multigame.repository.StarredGameRepository;
import com.ddockddack.domain.multigame.request.paging.PageConditionReq;
import com.ddockddack.domain.multigame.response.MultiGameRes;
import com.ddockddack.domain.multigame.response.ReportedGameRes;
import com.ddockddack.domain.multigame.response.StarredGameRes;
import com.ddockddack.domain.report.entity.ReportType;
import com.ddockddack.domain.report.entity.ReportedGame;
import com.ddockddack.domain.report.repository.ReportedGameRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class MultiGameServiceTest {
    private final Long memberOneId = 1L;
    private final Long memberTwoId = 2L;
    private final Long gameOneId = 2L;

    @InjectMocks
    MultiGameService multiGameService;
    @Mock
    ReportedGameRepository reportedGameRepository;
    @Mock
    StarredGameRepository starredGameRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    MultiGameRepository multiGameRepository;

    Logger log;

    private Member memberOne;
    private Member memberTwo;
    private MultiGame multiGameOne;


    @Transactional
    @BeforeEach
    public void setUp() {
        log = (Logger) LoggerFactory.getLogger(ReportedGameRepository.class);

        // member
        memberOne = Member.builder()
            .email("test@test.com")
            .nickname("테스터닉네임")
            .profile("testerProfile.jpg")
            .role(Role.MEMBER)
            .build();
        memberOne.setId(memberOneId);
        memberOne.setReleaseDate(LocalDate.now().plusDays(30));

        memberTwo = Member.builder()
            .email("test2@test.com")
            .nickname("테스터닉네임 2")
            .profile("tester2Profile.jpg")
            .role(Role.MEMBER)
            .build();
        memberTwo.setId(memberTwoId);
        memberTwo.setReleaseDate(LocalDate.now().plusDays(30));

        // multiGame
        multiGameOne = MultiGame.builder()
            .member(memberTwo)
            .title("멀티게임 1번 타이틀")
            .description("멀티게임 1번의 설명")
            .thumbnail("멀티게임 1번 썸네일.jpeg")
            .build();


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
    @DisplayName("게임 신고")
    void reportGame() {
        // given
        when(reportedGameRepository.existsByReportMemberIdAndMultiGameId(any(Long.class), any(Long.class))).thenReturn(false);
        when(multiGameRepository.findById(any(Long.class))).thenReturn(Optional.of(multiGameOne));
        when(memberRepository.getReferenceById(any(Long.class))).thenReturn(memberTwo);

        ReportedGame reportedGame = ReportedGame.builder()
            .multiGame(multiGameOne)
            .reportMember(memberOne)
            .reportedMember(multiGameOne.getMember())
            .reportType(ReportType.SPAM)
            .build();

        when(reportedGameRepository.save(any(ReportedGame.class))).thenReturn(reportedGame);

        // when
        multiGameService.reportGame(memberOneId, gameOneId, ReportType.SPAM);

        // then
        assertThat(reportedGameRepository.save(reportedGame).getMultiGame().getId()).isEqualTo(multiGameOne.getId());
        assertThat(reportedGame.getReportedMember().getId()).isEqualTo(memberTwoId);

        // verify
        verify(reportedGameRepository, times(1)).existsByReportMemberIdAndMultiGameId(memberOneId, gameOneId);
        verify(multiGameRepository, times(1)).findById(gameOneId);
        verify(memberRepository, times(1)).getReferenceById(memberOneId);
        verify(reportedGameRepository, times(1)).save(reportedGame);


    }

    @Test
    @DisplayName("모든 게임 리스트 조회")
    void findAllGamesByMemberId() {
        // given
        List<MultiGameRes> multiGameResList = new ArrayList<>();
        MultiGameRes multiGameRes = makeMultiGameRes();
        multiGameResList.add(multiGameRes);
        multiGameResList.add(multiGameRes);
        PageImpl<MultiGameRes> multiGameResPage = new PageImpl<>(multiGameResList);

        PageConditionReq pageConditionReq = PageConditionReq.builder()
            .page(1)
            .build();

        when(memberRepository.findById(memberOneId)).thenReturn(Optional.of(memberOne));
        when(multiGameRepository.findAllByMemberId(memberOneId, pageConditionReq)).thenReturn(multiGameResPage);

        // when
        PageImpl<MultiGameRes> allGamesByMemberId = multiGameService.findAllGamesByMemberId(memberOneId, pageConditionReq);

        // then
        assertThat(allGamesByMemberId.getTotalPages()).isEqualTo(1);
        assertThat(allGamesByMemberId.getTotalElements()).isEqualTo(2);

        // verify
        verify(memberRepository, times(1)).findById(memberOneId);
        verify(multiGameRepository, times(1)).findAllByMemberId(memberOneId, pageConditionReq);
    }


    @Test
    @DisplayName("즐겨찾기한 게임 리스트 조회")
    void findAllStarredGames() {
        // given
        List<StarredGameRes> allStarredGames = new ArrayList<>();
        StarredGameRes starredGameRes = makeStarredGameRes();
        allStarredGames.add(starredGameRes);
        when(memberRepository.findById(memberOneId)).thenReturn(Optional.of(memberOne));
        doReturn(allStarredGames).when(starredGameRepository).findAllStarredGame(memberOneId);

        // when
        List<StarredGameRes> result  = multiGameService.findAllStarredGames(memberOneId);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getGameId()).isEqualTo(gameOneId);
        assertThat(result.get(0).getGameTitle()).isEqualTo("테스트 게임 타이틀");

        // verify
        verify(memberRepository, times(1)).findById(memberOneId);
        verify(starredGameRepository, times(1)).findAllStarredGame(memberOneId);
    }


    @Test
    @DisplayName("신고된 게임 리스트 조회")
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
        reportedGameRes.setGameId(gameOneId);
        reportedGameRes.setReason("테스트 사유입니다.");
        reportedGameRes.setGameTitle("테스트 게임 타이틀");
        reportedGameRes.setReportMemberNickname("테스트 신고자 닉네임");
        reportedGameRes.setReportedMemberNickname("테스트 피신고자 닉네임");

        return reportedGameRes;
    }

    private StarredGameRes makeStarredGameRes() {
        StarredGameRes starredGameRes = new StarredGameRes();
        starredGameRes.setGameId(gameOneId);
        starredGameRes.setGameTitle("테스트 게임 타이틀");
        starredGameRes.setGameDesc("테스트 게임 상세 설명");
        starredGameRes.setCreator("creator");
        starredGameRes.setIsStarred(1);
        starredGameRes.setStarredCnt(10);
        starredGameRes.setPopularity(100);
        starredGameRes.setThumbnail("테스트 썸네일.jpeg");

        return starredGameRes;
    }

    private MultiGameRes makeMultiGameRes() {
        MultiGameRes multiGameRes = new MultiGameRes();
        multiGameRes.setGameId(gameOneId);
        multiGameRes.setGameTitle("테스트 게임 타이틀");
        multiGameRes.setGameDesc("테스트 게임 상세 설명");
        multiGameRes.setCreator("creator");
        multiGameRes.setIsStarred(1);
        multiGameRes.setStarredCnt(10);
        multiGameRes.setPopularity(100);
        multiGameRes.setThumbnail("테스트 썸네일.jpeg");

        return multiGameRes;
    }
}
