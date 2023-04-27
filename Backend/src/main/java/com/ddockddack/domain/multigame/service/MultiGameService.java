package com.ddockddack.domain.multigame.service;

import com.ddockddack.domain.multigame.entity.MultiGame;
import com.ddockddack.domain.multigame.entity.GameImage;
import com.ddockddack.domain.multigame.entity.StarredGame;
import com.ddockddack.domain.multigame.repository.GameImageRepository;
import com.ddockddack.domain.multigame.repository.MultiGameRepository;
import com.ddockddack.domain.multigame.repository.StarredGameRepository;
import com.ddockddack.domain.multigame.request.GameImageModifyReq;
import com.ddockddack.domain.multigame.request.GameImageParam;
import com.ddockddack.domain.multigame.request.GameModifyReq;
import com.ddockddack.domain.multigame.request.GameSaveReq;
import com.ddockddack.domain.multigame.response.MultiGameDetailRes;
import com.ddockddack.domain.multigame.response.MultiGameRes;
import com.ddockddack.domain.multigame.response.ReportedGameRes;
import com.ddockddack.domain.multigame.response.StarredGameRes;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.entity.Role;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.report.entity.ReportType;
import com.ddockddack.domain.report.entity.ReportedGame;
import com.ddockddack.domain.report.repository.ReportedGameRepository;
import com.ddockddack.global.aws.AwsS3;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AccessDeniedException;
import com.ddockddack.global.error.exception.AlreadyExistResourceException;
import com.ddockddack.global.error.exception.ImageExtensionException;
import com.ddockddack.global.error.exception.NotFoundException;
import com.ddockddack.global.oauth.MemberDetail;
import com.ddockddack.domain.multigame.request.paging.PageConditionReq;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MultiGameService {

    private final MultiGameRepository multiGameRepository;
    private final GameImageRepository gameImageRepository;
    private final MemberRepository memberRepository;
    private final StarredGameRepository starredGameRepository;
    private final ReportedGameRepository reportedGameRepository;
    private final AwsS3 awsS3;

    /**
     * 게임 목록 조회
     *
     * @param memberId
     * @param pageConditionReq
     * @return
     */
    @Transactional(readOnly = true)
    public PageImpl<MultiGameRes> findAllGames(Long memberId, PageConditionReq pageConditionReq) {

        return multiGameRepository.findAllBySearch(memberId, pageConditionReq);
    }

    /**
     * 게임 상세 조회
     *
     * @param gameId
     * @return
     */
    @Transactional(readOnly = true)
    public MultiGameDetailRes findGame(Long gameId) {
        List<MultiGameDetailRes> result = multiGameRepository.findGame(gameId);
        if (result.size() == 0) {
            throw new NotFoundException(ErrorCode.GAME_NOT_FOUND);
        }
        return result.get(0);
    }

    /**
     * 게임 생성
     *
     * @param gameSaveReq
     * @return gameId
     */
    public Long saveGame(Long memberId, GameSaveReq gameSaveReq) {
        // 게임 이미지 업로드
        List<String> fileNames = uploadGameImages(gameSaveReq);

        final Member member = memberRepository.getReferenceById(memberId);

        // 게임 생성
        MultiGame multiGame = gameSaveReq.toEntity(member, fileNames.get(0));
        Long gameId = multiGameRepository.save(multiGame).getId();

        List<GameImage> gameImages = createGameImage(multiGame, gameSaveReq, fileNames);
        // 리스트 안에 담긴 gameImage 객체 모두 등록
        gameImageRepository.saveAll(gameImages);

        return gameId;
    }

    /**
     * 게임 수정
     *
     * @param memberDetail
     * @param gameModifyReq
     */
    public void modifyGame(MemberDetail memberDetail, GameModifyReq gameModifyReq) {
        // 검증
        final MultiGame multiGame = checkAccessValidation(memberDetail, gameModifyReq.getGameId());

        // 게임 제목, 설명 수정
        multiGame.updateGame(gameModifyReq.getGameTitle(), gameModifyReq.getGameDesc());

        List<String> tempImage = new ArrayList<>();
        for (GameImageModifyReq gameImageModifyReq : gameModifyReq.getImages()) {
            GameImage getGameImage = gameImageRepository.findById(
                gameImageModifyReq.getGameImageId()).get();

            String imageExtension; // 이미지 확장자
            String contentType = gameImageModifyReq.getGameImage().getContentType();

            if (!contentType.contains("image/jpeg")) {
                throw new ImageExtensionException(ErrorCode.EXTENSION_NOT_ALLOWED);
            }
            String fileName = awsS3.multipartFileUpload(gameImageModifyReq.getGameImage());

            // 업데이트
            getGameImage.updateGameImage(fileName, gameImageModifyReq.getGameImageDesc());

        }
    }

    /**
     * 게임 삭제
     *
     * @param memberDetail
     * @param gameId
     */
    public void removeGame(MemberDetail memberDetail, Long gameId) {

        // 검증
        checkAccessValidation(memberDetail, gameId);
        gameImageRepository.deleteByMultiGameId(gameId);
        starredGameRepository.deleteByMultiGameId(gameId);
        reportedGameRepository.deleteByGameId(gameId);
        multiGameRepository.deleteById(gameId);

    }

    /**
     * 게임 즐겨 찾기
     *
     * @param memberId
     * @param gameId
     */
    public void starredGame(Long memberId, Long gameId) {

        boolean isExist = starredGameRepository.existsByMemberIdAndMultiGameId(memberId, gameId);

        if (isExist) {
            throw new AlreadyExistResourceException(ErrorCode.ALREADY_EXIST_STTAREDGAME);
        }
        final MultiGame multiGame = multiGameRepository.getReferenceById(gameId);
        final Member member = memberRepository.getReferenceById(memberId);

        StarredGame starredGame = StarredGame.builder()
            .multiGame(multiGame)
            .member(member)
            .build();

        multiGame.increaseStarredCnt();
        starredGameRepository.save(starredGame);

    }

    /**
     * 게임 즐겨 찾기 취소
     *
     * @param memberId
     * @param gameId
     */
    public void unStarredGame(Long memberId, Long gameId) {

        // 검증
        final MultiGame multiGame = checkGameValidation(gameId);

        StarredGame getStarredGame = starredGameRepository.findByMemberIdAndMultiGameId(memberId, gameId)
            .orElseThrow(() ->
                new NotFoundException(ErrorCode.STARREDGAME_NOT_FOUND));

        multiGame.decreaseStarredCnt();
        starredGameRepository.delete(getStarredGame);
    }

    /**
     * 게임 신고
     *
     * @param memberId
     * @param gameId
     */
    public void reportGame(Long memberId, Long gameId, ReportType reportType) {

        // 이미 신고했는지 검증
        boolean isExist = reportedGameRepository.existsByReportMemberIdAndMultiGameId(memberId, gameId);

        if (isExist) {
            throw new AlreadyExistResourceException(ErrorCode.ALREADY_EXIST_REPORTEDGAME);
        }

        final MultiGame multiGame = checkGameValidation(gameId);
        final Member reportMember = memberRepository.getReferenceById(memberId);

        ReportedGame reportedGame = ReportedGame.builder()
            .multiGame(multiGame)
            .reportMember(reportMember)
            .reportedMember(multiGame.getMember())
            .reportType(reportType)
            .build();

        reportedGameRepository.save(reportedGame);
    }

    /**
     * 내가 만든 게임 전체 조회
     *
     * @param memberId
     * @return
     */
    @Transactional(readOnly = true)
    public PageImpl<MultiGameRes> findAllGamesByMemberId(Long memberId,
        PageConditionReq pageConditionReq) {
        memberRepository.findById(memberId).orElseThrow(() ->
            new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return multiGameRepository.findAllByMemberId(memberId, pageConditionReq);
    }

    /**
     * 내가 즐겨 찾기한 게임 목록 조회
     *
     * @param memberId
     * @return
     */
    @Transactional(readOnly = true)
    public List<StarredGameRes> findAllStarredGames(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() ->
            new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return starredGameRepository.findAllStarredGame(memberId);
    }

    /**
     * 신고 된 게임 목록 전체 조회하기
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<ReportedGameRes> findAllReportedGames() {

        return reportedGameRepository.findAllReportedGame();
    }

    /**
     * 게임 수정, 삭제 권한 검증
     *
     * @param memberDetail
     * @param gameId
     */
    private MultiGame checkAccessValidation(MemberDetail memberDetail, Long gameId) {

        // 존재하는 게임 인지 검증
        MultiGame multiGame = checkGameValidation(gameId);

        // 관리자면 바로 리턴
        if (memberDetail.getRole().equals(Role.ADMIN)) {
            return multiGame;
        }

        // 삭제 권한을 가진 유저인지 검증
        if ((memberDetail.getId() != multiGame.getMember().getId())) {
            throw new AccessDeniedException(ErrorCode.NOT_AUTHORIZED);
        }
        return multiGame;
    }

    /**
     * 게임 validation
     *
     * @param gameId
     */
    private MultiGame checkGameValidation(Long gameId) {
        // 존재하는 게임 인지 검증
        return multiGameRepository.findById(gameId).orElseThrow(() ->
            new NotFoundException(ErrorCode.GAME_NOT_FOUND));
    }

    /**
     * S3에 게임이미지 업로드
     *
     * @param gameSaveReq
     * @return
     */
    private List<String> uploadGameImages(GameSaveReq gameSaveReq) {
        List<String> fileNames = new ArrayList<>();
        for (GameImageParam gameImageParam : gameSaveReq.getImages()) {
            String contentType = gameImageParam.getGameImage().getContentType();

            // 이미지 확장자가 jpeg, png인 경우만 업로드 아닌경우 예외 발생
            if (!contentType.contains("image/jpeg")) {
                throw new ImageExtensionException(ErrorCode.EXTENSION_NOT_ALLOWED);
            }

            // 파일 업로드
            String fileName = awsS3.multipartFileUpload(gameImageParam.getGameImage());

            // 리스트에 추가
            fileNames.add(fileName);
        }
        return fileNames;
    }

    /**
     * 게임 이미지 Entity 생성
     *
     * @param multiGame
     * @param gameSaveReq
     * @param fileNames
     * @return
     */
    private List<GameImage> createGameImage(MultiGame multiGame, GameSaveReq gameSaveReq,
        List<String> fileNames) {
        List<GameImage> gameImages = new ArrayList<>();
        for (int idx = 0; idx < gameSaveReq.getImages().size(); idx++) {
            GameImage gameImage = gameSaveReq.getImages().get(idx)
                .toEntity(multiGame, fileNames.get(idx));
            // 리스트에 추가
            gameImages.add(gameImage);
        }
        return gameImages;
    }


}
