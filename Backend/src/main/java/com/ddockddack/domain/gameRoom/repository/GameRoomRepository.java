package com.ddockddack.domain.gameRoom.repository;

import com.ddockddack.domain.game.entity.Game;
import com.ddockddack.domain.game.entity.GameImage;
import com.ddockddack.domain.gameRoom.entity.GameRoomHistory;
import com.ddockddack.domain.gameRoom.response.GameMemberRes;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AccessDeniedException;
import com.ddockddack.global.error.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
@RequiredArgsConstructor
public class GameRoomRepository {

    private final Integer PIN_NUMBER_BOUND = 1_000_000;
    private final Random random = new Random();
    private final ObjectMapper mapper = new ObjectMapper();
    private final GameRoomHistoryRepository gameRoomHistoryRepository;
    @Value("${OPENVIDU_URL}")
    private String OPENVIDU_URL;
    @Value("${OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;
    private String OPENVIDU_HEADER;
    private Map<String, GameRoom> gameRooms = new ConcurrentHashMap<>();
    private OpenVidu openvidu;


    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
        OPENVIDU_HEADER = "Basic " + Base64.getEncoder().encodeToString(("OPENVIDUAPP:" + OPENVIDU_SECRET).getBytes());
        log.info("OPENVIDU_URL" + OPENVIDU_URL);
    }

    /**
     * 방 생성
     * @param game
     * @return
     * @throws OpenViduJavaClientException
     * @throws OpenViduHttpException
     */
    public String create(Game game) throws OpenViduJavaClientException, OpenViduHttpException {
        //핀 넘버 생성
        String pinNumber = createPinNumber();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("customSessionId", pinNumber);


        List<GameImage> gameImages = game.getImages();
        Collections.shuffle(gameImages);

        //생성한 pin으로 openvidu에 session 생성 요청
        SessionProperties properties = SessionProperties.fromJson(paramMap).build();
        openvidu.createSession(properties);

        //방 객체 생성 후 map에 저장
        GameRoom gameRoom = GameRoom.builder()
                .gameId(game.getId())
                .gameTitle(game.getTitle())
                .gameDescription(game.getDescription())
                .gameImages(gameImages)
                .pinNumber(pinNumber)
                .build();
        gameRooms.put(pinNumber, gameRoom);

        return pinNumber;
    }

    /**
     * 방 참가
     * @param pinNumber
     * @param member
     * @param nickname
     * @param clientIp
     * @return
     * @throws OpenViduJavaClientException
     * @throws OpenViduHttpException
     */
    public String join(String pinNumber, Member member, String nickname, String clientIp)
            throws OpenViduJavaClientException, OpenViduHttpException {
        //존재하는 pin인지 확인
        Session session = findSessionByPinNumber(pinNumber).orElseThrow(
                () -> new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));

        // 방 인원 제한 최대 7명
        if (openvidu.getActiveSession(pinNumber).getConnections().size() == 7) {
            throw new AccessDeniedException(ErrorCode.MAXIMUM_MEMBER);
        }

        //openvidu에 connection 요청
        ConnectionProperties properties = ConnectionProperties.fromJson(new HashMap<>()).build();
        Connection connection = session.createConnection(properties);

        // member를 gameMember으로 변환하여 gameRoom에 저장
        String socketId = connection.getConnectionId();
        GameMember gameMember = GameMember.builder()
            .socketId(socketId)
            .member(member)
            .nickname(nickname)
            .clientIp(clientIp)
            .build();

        gameRooms.get(pinNumber)
            .getMembers()
            .put(socketId, gameMember);

        return connection.getToken();
    }

    /**
     * 세션 조회
     * @param pinNumber
     * @return
     */
    public Optional<Session> findSessionByPinNumber(String pinNumber) {
        return Optional.ofNullable(openvidu.getActiveSession(pinNumber));
    }

    /**
     * 핀 넘버 생성
     * @return
     */
    public String createPinNumber() {
        String pin = formatPin(random.nextInt(PIN_NUMBER_BOUND));
        while (gameRooms.containsKey(pin)) {
            pin = formatPin(random.nextInt(PIN_NUMBER_BOUND));
        }
        return pin;
    }

    /**
     * 핀 넘버 포맷팅
     * @param num
     * @return
     */
    public String formatPin(int num) {
        return String.format("%06d", num);
    }

    /**
     * 게임 멤버 삭제
     * @param pinNumber
     * @param sessionId
     */
    public void deleteGameMember(String pinNumber, String sessionId) {
        gameRooms.get(pinNumber).getMembers().remove(sessionId);
    }

    /**
     * 세션 삭제
     * @param pinNumber
     */
    public void deleteById(String pinNumber) {
        gameRooms.remove(pinNumber);
    }

    /**
     * 게임 방 조회
     * @param pinNumber
     * @return
     */
    public Optional<GameRoom> findById(String pinNumber) {
        return Optional.ofNullable(gameRooms.get(pinNumber));
    }

    /**
     * 게임 시작
     * @param pinNumber
     * @throws JsonProcessingException
     */
    public void startGame(String pinNumber) throws JsonProcessingException {
        GameRoom gameRoom = this.gameRooms.get(pinNumber);
        gameRoom.start();

        String signal = createSignal(pinNumber, "roundStart", String.valueOf(gameRoom.getRound()));
        sendSignal(signal);
    }

    /**
     * 다음 라운드
     * @param pinNumber
     * @throws JsonProcessingException
     */
    public void nextRound(String pinNumber) throws JsonProcessingException {
        GameRoom gameRoom = this.gameRooms.get(pinNumber);

        String signal = createSignal(pinNumber, "roundStart", String.valueOf(gameRoom.getRound()));
        sendSignal(signal);
    }

    /**
     * 게임 점수 저장
     * @param pinNumber
     * @param sessionId
     * @param byteImage
     * @param rawScore
     * @throws JsonProcessingException
     */
    public void saveScore(String pinNumber, String sessionId, byte[] byteImage, int rawScore)
            throws JsonProcessingException {
        GameRoom gameRoom = this.gameRooms.get(pinNumber);
        GameMember gameMember = gameRoom.getMembers().get(sessionId);
        gameMember.getImages().add(byteImage);
        gameMember.changeRoundScore(rawScore);
        gameRoom.increaseScoreCnt();

        if (gameRoom.getScoreCount() == gameRoom.getMembers().size()) {
            List<GameMemberRes> roundResultData = findRoundResult(gameRoom);
            int maxRoundScore = Collections.max(roundResultData, Comparator.comparing(GameMemberRes::getRoundScore)).getRoundScore();
            for (GameMember member : gameRoom.getMembers().values()) {
                int scaledRoundScore = (int) (((double) member.getRoundScore() / maxRoundScore) * 100); //max score per round is +100 point
                member.changeScaledRoundScore(scaledRoundScore);
                member.changeTotalScore(member.getTotalScore() + scaledRoundScore);
            }

            String resultData = mapper.writeValueAsString(roundResultData);
            String signal = createSignal(pinNumber, "roundResult", resultData);

            sendSignal(signal);
            gameRoom.resetScoreCnt();
            gameRoom.increaseRound();
        }
    }

    /**
     * 최종 결과 반환
     * @param pinNumber
     * @throws JsonProcessingException
     */
    public void finalResult(String pinNumber) throws JsonProcessingException {
        GameRoom gameRoom = this.gameRooms.get(pinNumber);
        String resultData = mapper.writeValueAsString(findFinalResult(gameRoom));

        String signal = createSignal(pinNumber, "finalResult", resultData);
        sendSignal(signal);
    }


    /**
     * 시그널 셍성
     * @param pinNumber
     * @param signalName
     * @param data
     * @return
     * @throws JsonProcessingException
     */
    private String createSignal(String pinNumber, String signalName, String data) throws JsonProcessingException {
        GameSignalReq req = GameSignalReq.builder()
                .session(pinNumber)
                .type(signalName)
                .data(data)
                .build();

        String stringReq = mapper.writeValueAsString(req);
        return stringReq;
    }

    /**
     * 시그널 보내기
     * @param signal
     */
    private void sendSignal(String signal) {
        RestTemplate restTemplate = new RestTemplate();

        String url = OPENVIDU_URL + "/api/signal";

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", OPENVIDU_HEADER);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(signal, headers);
        restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
    }


    /**
     * 게임 유저의 이미지 조회
     * @param pinNumber
     * @param sessionId
     * @param index
     * @return
     */
    public byte[] findByImageIndex(String pinNumber, String sessionId, int index) {
        GameMember gameMember = gameRooms.get(pinNumber).getMembers().get(sessionId);
        return gameMember.getImages().get(index);
    }

    /**
     * 라운드 결과 반환
     * @param gameRoom
     * @return
     */
    public List<GameMemberRes> findRoundResult(GameRoom gameRoom) {
        List<GameMember> members = new ArrayList<>(gameRoom.getMembers().values());
        PriorityQueue<GameMember> pq = new PriorityQueue<>((a, b) -> b.getRoundScore() - a.getRoundScore());
        pq.addAll(members);
        List<GameMemberRes> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (pq.isEmpty()) break;
            result.add(GameMemberRes.from(pq.poll(), gameRoom.getRound() - 1));
        }
        return result;
    }

    /**
     * 최종 결과 반환 후 게임 이력 저장
     * @param gameRoom
     * @return
     */
    @Transactional
    public List<GameMemberRes> findFinalResult(GameRoom gameRoom) {
        List<GameMember> members = new ArrayList<>(gameRoom.getMembers().values());
        PriorityQueue<GameMember> pq = new PriorityQueue<>((a, b) -> b.getTotalScore() - a.getTotalScore());
        pq.addAll(members);
        List<GameMemberRes> finalResult = new ArrayList<>();
        List<GameRoomHistory> historyList = new ArrayList<>();
        int ranking = 1;
        while (!pq.isEmpty()) {
            GameMember gameMember = pq.poll();
            finalResult.add(GameMemberRes.from(gameMember));
            Optional<Member> member = Optional.ofNullable(gameMember.getMember());
            if (member.isPresent()) {
                Member getMember = member.get();
                GameRoomHistory grh = GameRoomHistory
                        .builder()
                        .gameTitle(gameRoom.getGameTitle())
                        .memberId(getMember.getId())
                        .ranking(ranking)
                        .build();
                historyList.add(grh);
            }
            ranking++;
        }
        if (historyList.size() != 0) {
            gameRoomHistoryRepository.saveAll(historyList);
        }
        return finalResult;
    }
}