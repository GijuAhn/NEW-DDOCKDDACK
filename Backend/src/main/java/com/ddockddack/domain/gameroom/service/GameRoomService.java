package com.ddockddack.domain.gameroom.service;

import com.ddockddack.domain.gameroom.entity.GameMember;
import com.ddockddack.domain.gameroom.entity.GameRoom;
import com.ddockddack.domain.gameroom.repository.GameMemberRedisRepository;
import com.ddockddack.domain.gameroom.repository.GameRoomRedisRepository;
import com.ddockddack.domain.gameroom.request.ScoringReq;
import com.ddockddack.domain.gameroom.response.GameMemberRes;
import com.ddockddack.domain.gameroom.response.GameRoomRes;
import com.ddockddack.domain.gameroom.util.OpenViduManager;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.multigame.entity.GameImage;
import com.ddockddack.domain.multigame.entity.MultiGame;
import com.ddockddack.domain.multigame.repository.MultiGameRepository;
import com.ddockddack.global.aws.AwsS3;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AccessDeniedException;
import com.ddockddack.global.error.exception.NotFoundException;
import com.ddockddack.global.util.PinNumberGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final MultiGameRepository multiGameRepository;
    private final MemberRepository memberRepository;
    private final AwsS3 awsS3;
    private final GameRoomRedisRepository gameRoomRedisRepository;
    private final GameMemberRedisRepository gameMemberRedisRepository;
    private final PinNumberGenerator pinNumberGenerator;
    private final OpenViduManager openViduManager;
    private final RestTemplate restTemplate;
    @Value("${IMAGE_PATH}")
    private String IMAGE_PATH;

    /**
     * 방 생성
     *
     * @param gameId
     * @return
     * @throws OpenViduJavaClientException
     * @throws OpenViduHttpException
     */
    @Transactional
    public String createGameRoom(Long gameId)
        throws OpenViduJavaClientException, OpenViduHttpException {

        final String pinNumber = createPinNumber();

        final MultiGame multiGame = multiGameRepository.findById(gameId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        List<GameImage> gameImages = multiGame.getImages();
        Collections.shuffle(gameImages);

        openViduManager.createSession(pinNumber);

        final GameRoom gameRoom = GameRoom.builder()
            .gameId(multiGame.getId())
            .gameTitle(multiGame.getTitle())
            .gameDescription(multiGame.getDescription())
            .gameImages(gameImages)
            .pinNumber(pinNumber)
            .build();

        gameRoomRedisRepository.save(gameRoom);

        return pinNumber;

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
    public GameRoomRes joinGameRoom(String pinNumber, Long memberId, String nickname,
        String clientIp)
        throws OpenViduJavaClientException, OpenViduHttpException {

        Member member = null;
        //로그인 한 유저면 memberId로 검색해서 넘겨줌
        if (memberId != null) {
            member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        }

        final GameRoom gameRoom = gameRoomRedisRepository.findById(pinNumber).orElseThrow(() ->
            new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));

        if (gameRoom.isStarted()) {
            throw new AccessDeniedException(ErrorCode.ALREADY_STARTED_GAME);
        }

        //중복 접속 방지 (허용하려면 주석처리)
//        for (GameMember gameMember : gameRoom.getMembers().values()) {
//            if (clientIp.equals(gameMember.getClientIp())) {
//                throw new AccessDeniedException(ErrorCode.NOT_AUTHORIZED);
//            }
//        }

        //존재하는 session 인지 확인
        Session session = openViduManager.findSessionByPinNumber(pinNumber);

        // 방 인원 제한 최대 7명
        if (session.getConnections().size() == 5) {
            throw new AccessDeniedException(ErrorCode.MAXIMUM_MEMBER);
        }

        //openvidu에 connection 요청
        log.info("connections size : {}", session.getConnections().size());

        Connection connection = openViduManager.createConnection(session);
        String socketId = connection.getConnectionId();

        // member를 gameMember으로 변환하여 gameRoom에 저장
        GameMember gameMember = GameMember.builder()
            .socketId(socketId)
            .pinNumber(pinNumber)
            .member(member)
            .nickname(nickname)
            .clientIp(clientIp)
            .build();

        gameMemberRedisRepository.save(gameMember);

        GameRoomRes gameRoomRes = GameRoomRes.of(gameRoom,
            session.getConnections().size() == 1 ? true : false);
        gameRoomRes.setToken(connection.getToken());
        return gameRoomRes;
    }

    /**
     * 게임방 멤버 삭제
     *
     * @param socketId
     */
    public void removeGameMember(String socketId)
        throws OpenViduJavaClientException, OpenViduHttpException {
        gameMemberRedisRepository.deleteById(socketId);
        openViduManager.fetch();
    }


    /**
     * 게임 방 삭제
     *
     * @param pinNumber
     * @throws OpenViduJavaClientException
     * @throws OpenViduHttpException
     */
    public void removeGameRoom(String pinNumber)
        throws OpenViduJavaClientException, OpenViduHttpException {

        final List<GameMember> gameMembers = gameMemberRedisRepository.findByPinNumber(pinNumber);
        gameMembers.forEach(gameMember -> {
            gameMemberRedisRepository.deleteById(gameMember.getSocketId());
        });
        gameRoomRedisRepository.deleteById(pinNumber);
        openViduManager.fetch();
    }


    /**
     * 게임 시작
     *
     * @param pinNumber
     */
    @Transactional
    public void startGame(String pinNumber) throws JsonProcessingException {
        // 현재 존재하는 게임 방인지 확인
        final GameRoom gameRoom = gameRoomRedisRepository.findById(pinNumber).orElseThrow(() ->
            new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));

        // 존재하는 게임인지 확인
        final MultiGame multiGame = multiGameRepository.findById(gameRoom.getGameId())
            .orElseThrow(() ->
                new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        // 게임 play count +1 증가
        multiGame.increasePlayCount();
        gameRoom.start();

        openViduManager.sendSignal(pinNumber, "roundStart", gameRoom.getRound());
    }

    /**
     * 게임 멤버 이미지 채점 및 저장
     *
     * @param scoringReq
     * @throws Exception
     */
    public void scoringUserImage(ScoringReq scoringReq) throws IOException {
        gameRoomRedisRepository.findById(scoringReq.getPinNumber()).orElseThrow(() ->
            new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));
        Map<String, String> event = new HashMap<>();
        event.put("target", IMAGE_PATH+scoringReq.getGameImage());
        String imageUrl = awsS3.multipartFileUpload(scoringReq.getMemberGameImage());
        event.put("input", IMAGE_PATH+imageUrl);

        Integer rawScore = 0;
        try {
            rawScore = restTemplate.postForObject(
                "https://s1faxc16gj.execute-api.ap-northeast-2.amazonaws.com/prod1/simil", event,
                Integer.class);
        } catch (Exception e) {
            log.error("restTemplate. post ForObject Error : {}",e.getMessage());
        }
        saveScore(scoringReq.getPinNumber(), scoringReq.getSocketId(), imageUrl, rawScore);

    }

    /**
     * 다음 라운드로 진행
     *
     * @param pinNumber
     * @return
     */
    public void nextRound(String pinNumber) throws JsonProcessingException {
        final int round = gameRoomRedisRepository.findById(pinNumber).orElseThrow(() ->
            new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND)).getRound();

        openViduManager.sendSignal(pinNumber, "roundStart", round);
    }

    /**
     * 최종 결과
     *
     * @param pinNumber
     * @return
     */
    public void getFinalResult(String pinNumber) throws JsonProcessingException {
        final List<GameMember> gameMembers = gameMemberRedisRepository.findByPinNumber(pinNumber);
        List<GameMemberRes> finalResult = findFinalResult(gameMembers);
        openViduManager.sendSignal(pinNumber, "finalResult", finalResult);
    }

    /**
     * 핀 넘버 생성
     *
     * @return
     */
    private String createPinNumber() {
        String pin = pinNumberGenerator.createPinNumber();
        while (gameRoomRedisRepository.existsById(pin)) {
            pin = pinNumberGenerator.createPinNumber();
        }
        return pin;
    }

    /**
     * 게임 점수 저장
     *
     * @param pinNumber
     * @param socketId
     * @param imageUrl
     * @param rawScore
     * @throws JsonProcessingException
     */
    private synchronized void saveScore(String pinNumber, String socketId, String imageUrl,
        int rawScore)
        throws JsonProcessingException {

        final GameRoom gameRoom = gameRoomRedisRepository.findById(pinNumber).orElseThrow(() ->
            new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));

        final GameMember gameMember = gameMemberRedisRepository.findById(socketId).get();

        gameMember.getImages().add(imageUrl);
        gameMember.changeRoundScore(rawScore);
        gameRoom.increaseScoreCnt();
        gameMemberRedisRepository.save(gameMember);

        log.info("openvidu connection size : {}",
            openViduManager.getConnections(pinNumber).size());
        if (gameRoom.getScoreCount() == openViduManager.getConnections(pinNumber).size()) {
            List<GameMember> gameMembers = gameMemberRedisRepository.findByPinNumber(pinNumber);

            List<GameMemberRes> roundResultData = findRoundResult(gameMembers, gameRoom.getRound());

            int maxRoundScore = Collections.max(roundResultData,
                Comparator.comparing(GameMemberRes::getRoundScore)).getRoundScore();

            for (GameMember member : gameMembers) {

                int scaledRoundScore = (int) (((double) member.getRoundScore() / maxRoundScore)
                    * 100); //max score per round is +100 point

                member.changeScaledRoundScore(scaledRoundScore);
                member.changeTotalScore(member.getTotalScore() + scaledRoundScore);

                gameMemberRedisRepository.save(member);
            }

            openViduManager.sendSignal(pinNumber, "roundResult", roundResultData);
            gameRoom.resetScoreCnt();
            gameRoom.increaseRound();
        }

        gameRoomRedisRepository.save(gameRoom);

    }

    /**
     * 라운드 결과 반환
     *
     * @param gameMembers
     * @return
     */
    private List<GameMemberRes> findRoundResult(List gameMembers, int round) {

        PriorityQueue<GameMember> pq = new PriorityQueue<>(
            (a, b) -> b.getRoundScore() - a.getRoundScore());

        pq.addAll(gameMembers);
        List<GameMemberRes> result = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            if (pq.isEmpty()) {
                break;
            }
            result.add(GameMemberRes.of(pq.poll(), round - 1));
        }
        return result;
    }

    /**
     * 최종 결과 반환 후 게임 이력 저장
     *
     * @param gameMembers
     * @return
     */
    private List<GameMemberRes> findFinalResult(List gameMembers) {
        PriorityQueue<GameMember> pq = new PriorityQueue<>(
            (a, b) -> b.getTotalScore() - a.getTotalScore());
        pq.addAll(gameMembers);
        List<GameMemberRes> finalResult = new ArrayList<>();
        while (!pq.isEmpty()) {
            GameMember gameMember = pq.poll();
            finalResult.add(GameMemberRes.from(gameMember));
        }

        return finalResult;
    }

}