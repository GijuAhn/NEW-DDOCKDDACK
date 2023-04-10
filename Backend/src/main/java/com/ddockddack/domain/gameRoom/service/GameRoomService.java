package com.ddockddack.domain.gameRoom.service;

import com.ddockddack.domain.game.entity.Game;
import com.ddockddack.domain.game.repository.GameRepository;
import com.ddockddack.domain.gameRoom.entity.GameRoomHistory;
import com.ddockddack.domain.gameRoom.repository.GameMember;
import com.ddockddack.domain.gameRoom.repository.GameRoom;
import com.ddockddack.domain.gameRoom.repository.GameRoomHistoryRepository;
import com.ddockddack.domain.gameRoom.repository.GameRoomRepository;
import com.ddockddack.domain.gameRoom.response.GameMemberRes;
import com.ddockddack.domain.gameRoom.response.GameRoomHistoryRes;
import com.ddockddack.domain.gameRoom.response.GameRoomRes;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.similarity.service.EnsembleModel;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AccessDeniedException;
import com.ddockddack.global.error.exception.NotFoundException;
import com.ddockddack.global.service.AwsS3Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final GameRepository gameRepository;
    private final MemberRepository memberRepository;
    private final GameRoomHistoryRepository gameRoomHistoryRepository;
    private final AwsS3Service awsS3Service;
    private final EnsembleModel ensembleModel;


    /**
     * 방 생성
     *
     * @param gameId
     * @return
     * @throws OpenViduJavaClientException
     * @throws OpenViduHttpException
     */
    public String createRoom(Long gameId)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        return gameRoomRepository.create(game);
    }

    /**
     * 방 참가
     *
     * @param pinNumber
     * @param memberId
     * @param nickname
     * @return
     * @throws OpenViduJavaClientException
     * @throws OpenViduHttpException
     */
    public GameRoomRes joinRoom(String pinNumber, Long memberId, String nickname, String clientIp)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Member member = null;
        //로그인 한 유저면 memberId로 검색해서 넘겨줌
        if (memberId != null) {
            member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        }

        GameRoom gameRoom = this.findGameRoom(pinNumber);

        //중복 접속 방지 (허용하려면 주석처리)
        for (GameMember gameMember : gameRoom.getMembers().values()) {
            if (clientIp.equals(gameMember.getClientIp())) {
                throw new AccessDeniedException(ErrorCode.NOT_AUTHORIZED);
            }
        }

        String token = gameRoomRepository.join(pinNumber, member, nickname, clientIp);

        if (gameRoom.isStarted()) {
            throw new AccessDeniedException(ErrorCode.ALREADY_STARTED_GAME);
        }
        GameRoomRes gameRoomRes = GameRoomRes.of(gameRoom);
        gameRoomRes.setToken(token);
        return gameRoomRes;
    }

    /**
     * 게임방 멤버 삭제
     *
     * @param pinNumber
     * @param sessionId
     */
    public void removeGameMember(String pinNumber, String sessionId) {

        gameRoomRepository.findSessionByPinNumber(pinNumber).orElseThrow(() ->
                new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));

        gameRoomRepository.deleteGameMember(pinNumber, sessionId);
    }

    /**
     * 게임방 삭제
     *
     * @param pinNumber
     */
    public void removeGameRoom(String pinNumber) {
        gameRoomRepository.findSessionByPinNumber(pinNumber).orElseThrow(() ->
                new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));

        // 게임 삭제 콬드
        gameRoomRepository.deleteById(pinNumber);
    }

    /**
     * 방 정보 조회
     *
     * @param pinNumber
     * @return GameRoom
     */
    public GameRoom findGameRoom(String pinNumber) {

        return gameRoomRepository.findById(pinNumber).orElseThrow(() ->
                new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));
    }

    /**
     * 게임 시작
     *
     * @param pinNumber
     */
    @Transactional
    public void startGame(String pinNumber) throws JsonProcessingException {
        // 현재 존재하는 게임 방인지 확징
        gameRoomRepository.findSessionByPinNumber(pinNumber).orElseThrow(() ->
                new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));

        Long gameId = gameRoomRepository.findById(pinNumber).get().getGameId();

        // 존재하는 게임인지 확인
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        // 게임 play count +1 증가
        game.increasePlayCount();
        gameRoomRepository.updateGameRoom(pinNumber);

    }

    /**
     * 게임 시작 여부 조회
     *
     * @param pinNumber
     * @return
     */
    public Boolean isStartedGame(String pinNumber) {
        GameRoom gameRoom = gameRoomRepository.findById(pinNumber).orElseThrow(() ->
                new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));
        return gameRoom.isStarted();
    }

    /**
     * 게임 멤버 이미지 저장
     *
     * @param pinNumber
     * @param sessionId
     * @param param
     */
    public void scoringImage(String pinNumber, String sessionId, Map<String, String> param)
            throws Exception {
        gameRoomRepository.findById(pinNumber).orElseThrow(() ->
                new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));
        byte[] byteGameImage = awsS3Service.getObject(param.get("gameImage"));
        byte[] byteImage = Base64.decodeBase64(param.get("memberGameImage"));
        int rawScore = ensembleModel.CalculateSimilarity(byteGameImage, byteImage);
        gameRoomRepository.saveScore(pinNumber, sessionId, byteImage, rawScore);
    }

    /**
     * 라운드 결과 조회
     *
     * @param pinNumber
     * @param round
     * @return
     */
    public List<GameMemberRes> findRoundResult(String pinNumber, int round) {
        Map<String, GameMember> gameMembers = gameRoomRepository.findGameMembers(pinNumber);
        List<GameMember> members = new ArrayList<>(gameMembers.values());
        PriorityQueue<GameMember> pq = new PriorityQueue<>(
                (a, b) -> b.getRoundScore() - a.getRoundScore());
        pq.addAll(members);
        List<GameMemberRes> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (pq.isEmpty()) {
                break;
            }
            result.add(GameMemberRes.from(pq.poll(), round));
        }
        return result;
    }

    /**
     * 게임 이력 전체 조회
     *
     * @param memberId
     * @return
     */
    @Transactional(readOnly = true)
    public List<GameRoomHistoryRes> findAllRoomHistory(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() ->
                new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        List<GameRoomHistory> list = gameRoomHistoryRepository.findByMemberId(memberId);

        return list.stream().map(GameRoomHistoryRes::of).collect(Collectors.toList());
    }

    /**
     * 다음 라운드로 진행
     *
     * @param pinNumber
     * @return
     */
    public void nextRound(String pinNumber) throws JsonProcessingException {
        gameRoomRepository.nextRound(pinNumber);
    }

    /**
     * 최종 결과
     *
     * @param pinNumber
     * @return
     */
    public void finalResult(String pinNumber) throws JsonProcessingException {
        gameRoomRepository.finalResult(pinNumber);
    }
}