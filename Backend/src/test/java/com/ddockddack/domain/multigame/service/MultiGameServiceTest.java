package com.ddockddack.domain.multigame.service;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.entity.Role;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.multigame.entity.GameImage;
import com.ddockddack.domain.multigame.entity.MultiGame;
import com.ddockddack.domain.multigame.entity.StarredGame;
import com.ddockddack.domain.multigame.repository.GameImageRepository;
import com.ddockddack.domain.multigame.repository.MultiGameRepository;
import com.ddockddack.domain.multigame.repository.StarredGameRepository;
import com.ddockddack.domain.multigame.request.GameImageModifyReq;
import com.ddockddack.domain.multigame.request.GameImageParam;
import com.ddockddack.domain.multigame.request.GameModifyReq;
import com.ddockddack.domain.multigame.request.GameSaveReq;
import com.ddockddack.domain.multigame.request.paging.PageConditionReq;
import com.ddockddack.domain.multigame.response.*;
import com.ddockddack.domain.report.entity.ReportType;
import com.ddockddack.domain.report.entity.ReportedGame;
import com.ddockddack.domain.report.repository.ReportedGameRepository;
import com.ddockddack.global.aws.AwsS3;
import com.ddockddack.global.oauth.MemberDetail;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    @Mock
    GameImageRepository gameImageRepository;
    @Mock
    AwsS3 awsS3;

    Logger log;

    private Member memberOne;
    private Member memberTwo;
    private MultiGame multiGameOne;
    private StarredGame starredGame;


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


        // starredGame
        starredGame = StarredGame.builder()
            .multiGame(multiGameOne)
            .member(memberOne)
            .build();
    }

    @Test
    @DisplayName("모든 게임 조회")
    void findAllGames() {
        // given
        List<MultiGameRes> multiGameResList = new ArrayList<>();
        MultiGameRes multiGameRes = makeMultiGameRes();
        multiGameResList.add(multiGameRes);
        multiGameResList.add(multiGameRes);
        PageImpl<MultiGameRes> multiGameResPage = new PageImpl<>(multiGameResList);

        PageConditionReq pageConditionReq = PageConditionReq.builder()
            .page(1)
            .build();
        when(multiGameRepository.findAllBySearch(any(Long.class), any(PageConditionReq.class))).thenReturn(multiGameResPage);

        // when
        PageImpl<MultiGameRes> allGames = multiGameService.findAllGames(memberOneId, pageConditionReq);

        // then
        assertThat(allGames.getTotalPages()).isEqualTo(1);
        assertThat(allGames.getTotalElements()).isEqualTo(2);

        // verify


    }

    @Test
    @DisplayName("게임 조회")
    void findGame() {
        // given
        MultiGameDetailRes multiGameDetailRes = makeMultiGameDetailRes();
        List<MultiGameDetailRes> result = new ArrayList<>();
        result.add(multiGameDetailRes);

        when(multiGameRepository.findGame(anyLong())).thenReturn(result);

        // when
        MultiGameDetailRes game = multiGameService.findGame(1L);

        // then
        assertThat(game.getGameId()).isEqualTo(gameOneId);

        // verify



    }

    private MultiGameDetailRes makeMultiGameDetailRes() {
        MultiGameDetailRes multiGameDetailRes = new MultiGameDetailRes();
        multiGameDetailRes.setGameId(gameOneId);
        multiGameDetailRes.setGameTitle(multiGameOne.getTitle());
        multiGameDetailRes.setGameDesc(multiGameOne.getDescription());
        GameImageRes gameImageRes = makeGameImageRes();
        List<GameImageRes> images = new ArrayList<>();
        images.add(gameImageRes);
        multiGameDetailRes.setImages(images);

        return multiGameDetailRes;
    }

    private GameImageRes makeGameImageRes() {
        GameImageRes gameImageRes = new GameImageRes();
        gameImageRes.setGameImageId(1L);
        gameImageRes.setGameImage("game Img");
        gameImageRes.setGameImageDesc("게임 이미지 설명");
        return gameImageRes;
    }

    @Test
    @DisplayName("게임 저장")
    @Transactional
    @Order(1)
    void saveGame() {
        // given
        GameSaveReq gameSaveReq = makeGameSaveReq();

        when(memberRepository.getReferenceById(any(Long.class))).thenReturn(memberOne);
        when(awsS3.multipartFileUpload(any(MultipartFile.class))).thenReturn("파일네임");
        when(multiGameRepository.save(any(MultiGame.class))).thenReturn(multiGameOne);

        // when
        Long answer = multiGameService.saveGame(memberOneId, gameSaveReq);

        // then
        assertThat(answer).isEqualTo(null);

        // verify
        verify(gameImageRepository, times(1)).saveAll(any());
        verify(multiGameRepository, times(1)).save(any());
        verify(awsS3, times(2)).multipartFileUpload(any());
        verify(memberRepository, times(1)).getReferenceById(anyLong());
    }

    @Test
    @Transactional
    @DisplayName("게임 수정")
    @Order(2)
    void modifyGame() {
        // given
        MemberDetail memberDetail = new MemberDetail("tempToken", 2L, Role.MEMBER);
        GameModifyReq gameModifyReq = makeGameModifyReq();
        GameImage gameImage1 = makeGameImage(multiGameOne, "게임이미지1 설명");
        GameImage gameImage2 = makeGameImage(multiGameOne, "게임이미지2 설명");

        when(multiGameRepository.findById(any(Long.class))).thenReturn(Optional.of(multiGameOne));
        when(gameImageRepository.findById(1L)).thenReturn(Optional.of(gameImage1));
        when(gameImageRepository.findById(2L)).thenReturn(Optional.of(gameImage2));
        when(awsS3.multipartFileUpload(any(MultipartFile.class))).thenReturn("이것으로 바꾸자");

        // when
        multiGameService.modifyGame(memberDetail, gameModifyReq);

        // then
        assertThat(multiGameOne.getTitle()).isEqualTo(gameModifyReq.getGameTitle());

        // verify
    }

    @Test
    @Transactional
    @DisplayName("게임 삭제")
    void removeGame() {
        // given
        MemberDetail memberDetail = new MemberDetail("tempToken", 2L, Role.MEMBER);
        when(multiGameRepository.findById(any(Long.class))).thenReturn(Optional.of(multiGameOne));

        // when
        multiGameService.removeGame(memberDetail, gameOneId);

        // then
        assertThat(memberDetail.getId()).isEqualTo(multiGameOne.getMember().getId());

        // verify
        verify(gameImageRepository, times(1)).deleteByMultiGameId(gameOneId);
        verify(starredGameRepository, times(1)).deleteByMultiGameId(gameOneId);
        verify(reportedGameRepository, times(1)).deleteByMultiGameId(gameOneId);
        verify(multiGameRepository, times(1)).deleteById(gameOneId);
    }

    @Test
    @Transactional
    @DisplayName("즐겨 찾기 등록")
    void starredGame() {
        // given
        int starredCnt = multiGameOne.getStarredCnt();

        when(starredGameRepository.existsByMemberIdAndMultiGameId(any(Long.class), any(Long.class))).thenReturn(false);
        when(multiGameRepository.getReferenceById(any(Long.class))).thenReturn(multiGameOne);
        when(memberRepository.getReferenceById(any(Long.class))).thenReturn(memberOne);
        when(starredGameRepository.save(any(StarredGame.class))).thenReturn(starredGame);

        starredGame = StarredGame.builder()
            .multiGame(multiGameOne)
            .member(memberOne)
            .build();

        // when
        multiGameService.starredGame(memberOneId, gameOneId);

        // then
        assertThat(multiGameOne.getStarredCnt()).isEqualTo(starredCnt + 1);

        // verify
        verify(starredGameRepository, times(1)).existsByMemberIdAndMultiGameId(memberOneId, gameOneId);
        verify(multiGameRepository, times(1)).getReferenceById(gameOneId);
        verify(memberRepository, times(1)).getReferenceById(memberOneId);
    }

    @Test
    @Transactional
    @DisplayName("즐겨 찾기 취소")
    void unStarredGame() {
        // given
        int starredCnt = multiGameOne.getStarredCnt();
        when(multiGameRepository.findById(any(Long.class))).thenReturn(Optional.of(multiGameOne));
        when(starredGameRepository.findByMemberIdAndMultiGameId(memberOneId, gameOneId)).thenReturn(Optional.of(starredGame));

        // when
        multiGameService.unStarredGame(memberOneId, gameOneId);

        // then
        assertThat(multiGameOne.getStarredCnt()).isEqualTo(starredCnt - 1);

        // verify
        verify(multiGameRepository, times(1)).findById(gameOneId);
        verify(starredGameRepository, times(1)).findByMemberIdAndMultiGameId(memberOneId, gameOneId);
        verify(starredGameRepository, times(1)).delete(starredGame);
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

    private GameModifyReq makeGameModifyReq() {
        GameModifyReq gameModifyReq = new GameModifyReq();
        gameModifyReq.setGameId(gameOneId);
        gameModifyReq.setGameTitle("바뀐 테스트 제목");
        gameModifyReq.setGameDesc("바뀐 테스트 설명");

        List<GameImageModifyReq> images = new ArrayList<>();
        MultipartFile multipartFile = new MockMultipartFile("테스트 파일", "testFileImage.jpeg", "image/jpeg", new byte[] {0, 1});
        GameImageModifyReq gameImageModifyReq1 = makeGameImageModifyReq(1L, multipartFile, "테스트 이미지1 설명");
        GameImageModifyReq gameImageModifyReq2 = makeGameImageModifyReq(2L, multipartFile, "테스트 이미지2 설명");
        images.add(gameImageModifyReq1);
        images.add(gameImageModifyReq2);

        gameModifyReq.setImages(images);

        return gameModifyReq;
    }

    private GameImageModifyReq makeGameImageModifyReq(long gameImageId, MultipartFile multipartFile, String gameImageDesc) {
        GameImageModifyReq gameImageModifyReq = new GameImageModifyReq();
        gameImageModifyReq.setGameImageId(gameImageId);
        gameImageModifyReq.setGameImage(multipartFile);
        gameImageModifyReq.setGameImageDesc(gameImageDesc);

        return gameImageModifyReq;

    }

    private GameSaveReq makeGameSaveReq() {
        GameSaveReq gameSaveReq = new GameSaveReq();
        gameSaveReq.setGameDesc("생성용 리퀘스트 설명");
        gameSaveReq.setGameTitle("생성용 리퀘스트 타이틀");

        List<GameImageParam> images = new ArrayList<>();
        GameImageParam gameImageParam1 = makeGameImagePara("파람1 설명");
        GameImageParam gameImageParam2 = makeGameImagePara("파람2 설명");

        images.add(gameImageParam1);
        images.add(gameImageParam2);

        gameSaveReq.setImages(images);
        return gameSaveReq;
    }

    private GameImageParam makeGameImagePara(String desc) {
        GameImageParam gameImageParam = new GameImageParam();
        gameImageParam.setGameImageDesc(desc);
        gameImageParam.setGameImage(new MockMultipartFile("imageParam테스트 파일", "testFileImage.jpeg", "image/jpeg", new byte[] {0, 1}));
        return gameImageParam;
    }

    private GameImage makeGameImage(MultiGame multiGame, String description) {
        GameImage gameImage = GameImage.builder()
            .multiGame(multiGame)
            .imageUrl("tempUrl")
            .description(description)
            .build();

        return gameImage;
    }
}
