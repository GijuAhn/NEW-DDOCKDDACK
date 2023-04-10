package com.ddockddack.domain.gameRoom.repository;

import com.ddockddack.domain.game.entity.Game;
import com.ddockddack.domain.game.entity.GameImage;
import com.ddockddack.domain.gameRoom.repository.GameRoom;
import com.ddockddack.domain.gameRoom.entity.GameRoomHistory;
import com.ddockddack.domain.gameRoom.request.GameSignalReq;
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

    public String join(String pinNumber, Member member, String nickname, String clientIp)
            throws OpenViduJavaClientException, OpenViduHttpException {
        //존재하는 pin인지 확인
        Session session = findSessionByPinNumber(pinNumber).orElseThrow(
                () -> new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));


        if (openvidu.getActiveSession(pinNumber).getConnections().size() == 7) {
            throw new AccessDeniedException(ErrorCode.MAXIMUM_MEMBER);
        }

        //openvidu에 connection 요청
        ConnectionProperties properties = ConnectionProperties.fromJson(new HashMap<>()).build();
        Connection connection = session.createConnection(properties);

        //member를 gameMember로 변환하여 gameRoom에 저장
        String socketId = connection.getConnectionId();
        GameMember gameMember = new GameMember(socketId, member, nickname, clientIp);
        gameRooms.get(pinNumber).getMembers().put(socketId, gameMember);

        return connection.getToken();
    }

    public Optional<Session> findSessionByPinNumber(String pinNumber) {
        return Optional.ofNullable(openvidu.getActiveSession(pinNumber));
    }

    public String createPinNumber() {
        String pin = intToPin(random.nextInt(PIN_NUMBER_BOUND));
        while (gameRooms.containsKey(pin)) {
            pin = intToPin(random.nextInt(PIN_NUMBER_BOUND));
        }
        return pin;
    }

    public String intToPin(int num) {
        return String.format("%06d", num);
    }

    public void deleteGameMember(String pinNumber, String sessionId) {
        gameRooms.get(pinNumber).getMembers().remove(sessionId);
    }

    public void deleteById(String pinNumber) {
        gameRooms.remove(pinNumber);
    }

    public Optional<GameRoom> findById(String pinNumber) {
        return Optional.ofNullable(gameRooms.get(pinNumber));
    }

    public void updateGameRoom(String pinNumber) throws JsonProcessingException {
        GameRoom gameRoom = this.gameRooms.get(pinNumber);
        gameRoom.start();

        String signal = createSignal(pinNumber, "roundStart", String.valueOf(gameRoom.getRound()));
        sendSignal(signal);
    }

    public void nextRound(String pinNumber) throws JsonProcessingException {
        GameRoom gameRoom = this.gameRooms.get(pinNumber);

        String signal = createSignal(pinNumber, "roundStart", String.valueOf(gameRoom.getRound()));
        sendSignal(signal);
    }

    public void saveScore(String pinNumber, String sessionId, byte[] byteImage, int rawScore)
            throws JsonProcessingException {
        GameRoom gameRoom = this.gameRooms.get(pinNumber);
        GameMember gameMember = gameRoom.getMembers().get(sessionId);
        gameMember.getImages().add(byteImage);
        gameMember.setRoundScore(rawScore);
        gameRoom.increaseScoreCnt();

        if (gameRoom.getScoreCount() == gameRoom.getMembers().size()) {
            List<GameMemberRes> roundResultData = findRoundResult(gameRoom);
            int maxRoundScore = Collections.max(roundResultData, Comparator.comparing(GameMemberRes::getRoundScore)).getRoundScore();
//            iterate every gameMember in the GameRoom and calculate scaledRoundScore and totalScore
//            members is Map(String, GameMember)
            for (GameMember member : gameRoom.getMembers().values()) {
                int scaledRoundScore = (int) (((double) member.getRoundScore() / maxRoundScore) * 100); //max score per round is +100 point
                member.setScaledRoundScore(scaledRoundScore);
                member.setTotalScore(member.getTotalScore() + scaledRoundScore);
            }

            String resultData = mapper.writeValueAsString(roundResultData);
            String signal = createSignal(pinNumber, "roundResult", resultData);

            sendSignal(signal);
            gameRoom.resetScoreCnt();
            gameRoom.increaseRound();
        }
    }

    public void finalResult(String pinNumber) throws JsonProcessingException {
        GameRoom gameRoom = this.gameRooms.get(pinNumber);
        String resultData = mapper.writeValueAsString(findFinalResult(gameRoom));

        String signal = createSignal(pinNumber, "finalResult", resultData);
        sendSignal(signal);
    }


    private String createSignal(String pinNumber, String signalName, String data) throws JsonProcessingException {
        GameSignalReq req = GameSignalReq.builder()
                .session(pinNumber)
                .type(signalName)
                .data(data)
                .build();

        String stringReq = mapper.writeValueAsString(req);
        return stringReq;
    }

    private void sendSignal(String signal) {
        RestTemplate restTemplate = new RestTemplate();

        String url = OPENVIDU_URL + "/api/signal";

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", OPENVIDU_HEADER);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(signal, headers);
        restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
    }


    public byte[] findByImageIndex(String pinNumber, String sessionId, int index) {
        GameMember gameMember = gameRooms.get(pinNumber).getMembers().get(sessionId);
        return gameMember.getImages().get(index);
    }

    public Map<String, GameMember> findGameMembers(String pinNumber) {
        return gameRooms.get(pinNumber).getMembers();
    }

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