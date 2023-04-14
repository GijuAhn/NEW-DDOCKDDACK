package com.ddockddack.domain.gameRoom.service;

import com.ddockddack.domain.game.entity.Game;
import com.ddockddack.domain.game.entity.GameImage;
import com.ddockddack.domain.game.repository.GameRepository;
import com.ddockddack.domain.gameRoom.entity.GameMember;
import com.ddockddack.domain.gameRoom.entity.GameRoom;
import com.ddockddack.domain.gameRoom.repository.GameMemberRedisRepository;
import com.ddockddack.domain.gameRoom.repository.GameRoomRedisRepository;
import com.ddockddack.domain.gameRoom.response.GameRoomRes;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.similarity.service.EnsembleModel;
import com.ddockddack.global.aws.AwsS3;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AccessDeniedException;
import com.ddockddack.global.error.exception.NotFoundException;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameRoomService {

    //    private final GameRoomRepository gameRoomRepository;
    private final GameRepository gameRepository;
    private final MemberRepository memberRepository;
    private final AwsS3 awsS3;
    private final EnsembleModel ensembleModel;
    private final GameRoomRedisRepository gameRoomRedisRepository;
    private final GameMemberRedisRepository gameMemberRedisRepository;

    private final Integer PIN_NUMBER_BOUND = 1_000_000;
    private final Random random = new Random();

    private OpenVidu openvidu;
    @Value("${OPENVIDU_URL}")
    private String OPENVIDU_URL;
    @Value("${OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;
    private String OPENVIDU_HEADER;

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
        OPENVIDU_HEADER = "Basic " + java.util.Base64.getEncoder()
            .encodeToString(("OPENVIDUAPP:" + OPENVIDU_SECRET).getBytes());
    }

    /**
     * 방 생성
     *
     * @param gameId
     * @return
     * @throws OpenViduJavaClientException
     * @throws OpenViduHttpException
     */
    public String createGameRoom(Long gameId)
        throws OpenViduJavaClientException, OpenViduHttpException {

        final Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        final String pinNumber = createPinNumber();
        Map<String, String> sessionPropertiesInfo = new HashMap<>();

        sessionPropertiesInfo.put("customSessionId", pinNumber);

        List<GameImage> gameImages = game.getImages();
        Collections.shuffle(gameImages);

        SessionProperties properties = SessionProperties.fromJson(sessionPropertiesInfo).build();
        openvidu.createSession(properties);

        final GameRoom gameRoom = GameRoom.builder()
            .gameId(game.getId())
            .gameTitle(game.getTitle())
            .gameDescription(game.getDescription())
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
        Session session = Optional.ofNullable(openvidu.getActiveSession(pinNumber))
            .orElseThrow(() ->
                new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));

        // 방 인원 제한 최대 7명
        if (session.getConnections().size() == 7) {
            throw new AccessDeniedException(ErrorCode.MAXIMUM_MEMBER);
        }

        //openvidu에 connection 요청
        ConnectionProperties properties = ConnectionProperties.fromJson(new HashMap<>()).build();
        Connection connection = session.createConnection(properties);

        // member를 gameMember으로 변환하여 gameRoom에 저장
        String socketId = connection.getConnectionId();

        GameMember gameMember = GameMember.builder()
            .socketId(socketId)
            .pinNumber(pinNumber)
            .member(member)
            .nickname(nickname)
            .clientIp(clientIp)
            .build();

        gameMemberRedisRepository.save(gameMember);

        GameRoomRes gameRoomRes = GameRoomRes.of(gameRoom, session.getConnections().size() == 1 ? true : false);
        gameRoomRes.setToken(connection.getToken());
        return gameRoomRes;
    }

    /**
     * 게임방 멤버 삭제
     *
     * @param socketId
     */
    public void removeGameMember(String socketId) {
        gameMemberRedisRepository.deleteById(socketId);
    }
//
    /**
     * 게임방 삭제
     *
     * @param pinNumber
     */
    public void removeGameRoom(String pinNumber) {

        final List<GameMember> gameMembers = gameMemberRedisRepository.findByPinNumber(pinNumber);

        gameMembers.forEach(gm ->
            gameMemberRedisRepository.deleteById(gm.getSocketId()));

        gameRoomRedisRepository.deleteById(pinNumber);
    }
//
//    /**
//     * 방 정보 조회
//     *
//     * @param pinNumber
//     * @return GameRoom
//     */
//    public GameRoom findGameRoom(String pinNumber) {
//
//        return gameRoomRepository.findById(pinNumber).orElseThrow(() ->
//            new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));
//    }
//
//    /**
//     * 게임 시작
//     *
//     * @param pinNumber
//     */
//    @Transactional
//    public void startGame(String pinNumber) throws JsonProcessingException {
//        // 현재 존재하는 게임 방인지 확징
//        gameRoomRepository.findSessionByPinNumber(pinNumber).orElseThrow(() ->
//            new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));
//
//        Long gameId = gameRoomRepository.findById(pinNumber).get().getGameId();
//
//        // 존재하는 게임인지 확인
//        Game game = gameRepository.findById(gameId).orElseThrow(() ->
//            new NotFoundException(ErrorCode.GAME_NOT_FOUND));
//
//        // 게임 play count +1 증가
//        game.increasePlayCount();
//        gameRoomRepository.startGame(pinNumber);
//
//    }
//
//    /**
//     * 게임 멤버 이미지 채점 및 저장
//     *
//     * @param pinNumber
//     * @param sessionId
//     * @param param
//     */
//    public void scoringUserImage(String pinNumber, String sessionId, Map<String, String> param)
//        throws Exception {
//        gameRoomRepository.findById(pinNumber).orElseThrow(() ->
//            new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));
//        byte[] byteGameImage = awsS3.getObject(param.get("gameImage"));
//        byte[] byteImage = Base64.decodeBase64(param.get("memberGameImage"));
//        int rawScore = ensembleModel.CalculateSimilarity(byteGameImage, byteImage);
//        gameRoomRepository.saveScore(pinNumber, sessionId, byteImage, rawScore);
//    }
//
//    /**
//     * 다음 라운드로 진행
//     *
//     * @param pinNumber
//     * @return
//     */
//    public void nextRound(String pinNumber) throws JsonProcessingException {
//        gameRoomRepository.nextRound(pinNumber);
//    }
//
//    /**
//     * 최종 결과
//     *
//     * @param pinNumber
//     * @return
//     */
//    public void getFinalResult(String pinNumber) throws JsonProcessingException {
//        gameRoomRepository.finalResult(pinNumber);
//    }

    /**
     * 핀 넘버 생성
     *
     * @return
     */
    private String createPinNumber() {
        String pin = formatPin(random.nextInt(PIN_NUMBER_BOUND));
        while (gameRoomRedisRepository.existsById(pin)) {
            pin = formatPin(random.nextInt(PIN_NUMBER_BOUND));
        }
        return pin;
    }

    /**
     * 핀 넘버 포맷팅
     *
     * @param num
     * @return
     */
    private String formatPin(int num) {
        return String.format("%06d", num);
    }

}