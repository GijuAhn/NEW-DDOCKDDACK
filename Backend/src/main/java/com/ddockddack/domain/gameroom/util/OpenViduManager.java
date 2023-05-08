package com.ddockddack.domain.gameroom.util;

import com.ddockddack.domain.gameroom.repository.GameSignalReq;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OpenViduManager {

    private final ObjectMapper mapper;
    private final OpenVidu openvidu;
    @Value("${OPENVIDU_API_URL}")
    private String OPENVIDU_API_URL;
    @Value("${OPENVIDU_HEADER}")
    private String OPENVIDU_HEADER;
    private final HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        headers.set("Authorization", OPENVIDU_HEADER);
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    public void createSession(String pinNumber)
        throws OpenViduJavaClientException, OpenViduHttpException {

        Map<String, String> sessionPropertiesInfo = new HashMap<>();
        sessionPropertiesInfo.put("customSessionId", pinNumber);
        SessionProperties properties = SessionProperties.fromJson(sessionPropertiesInfo).build();

        openvidu.createSession(properties);
    }

    public Session findSessionByPinNumber(String pinNumber) {
        return Optional.ofNullable(openvidu.getActiveSession(pinNumber))
            .orElseThrow(() ->
                new NotFoundException(ErrorCode.GAME_ROOM_NOT_FOUND));
    }

    public Connection createConnection(Session session)
        throws OpenViduJavaClientException, OpenViduHttpException {
        ConnectionProperties properties = ConnectionProperties.fromJson(new HashMap<>()).build();
        Connection connection = session.createConnection(properties);
        return connection;
    }

    public void fetch() throws OpenViduJavaClientException, OpenViduHttpException {
        this.openvidu.fetch();
    }

    public void sendSignal(String pinNumber, String signalName, Object data)
        throws JsonProcessingException {

        String resultData = mapper.writeValueAsString(data);
        GameSignalReq request = GameSignalReq.builder()
            .session(pinNumber)
            .type(signalName)
            .data(resultData)
            .build();

        String stringReq = mapper.writeValueAsString(request);
        HttpEntity<String> entity = new HttpEntity<>(stringReq, headers);
        restTemplate.exchange(OPENVIDU_API_URL, HttpMethod.POST, entity, String.class);
    }

    public List<Connection> getConnections(String pinNumber) {
        return openvidu.getActiveSession(pinNumber).getConnections();
    }


}
