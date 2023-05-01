package com.ddockddack.domain.gameroom.controller;

import com.ddockddack.domain.gameroom.request.ScoringReq;
import com.ddockddack.domain.gameroom.response.GameRoomRes;
import com.ddockddack.domain.gameroom.service.GameRoomService;
import com.ddockddack.global.oauth.MemberDetail;
import com.ddockddack.global.util.ClientUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game-rooms")
public class GameRoomApiController {

    private final GameRoomService gameRoomService;

    @PostMapping
    @Operation(summary = "게임방 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방 생성 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임")
    })
    public ResponseEntity<String> gameRoomCreate(@RequestParam Long gameId) {
        String pinNumber;
        try {
            pinNumber = gameRoomService.createGameRoom(gameId);
        } catch (OpenViduJavaClientException e) {
            throw new RuntimeException(e);
        } catch (OpenViduHttpException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(pinNumber, HttpStatus.OK);
    }

    @PostMapping("/{pinNumber}")
    @Operation(summary = "게임방 참가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방 참가 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임방")
    })
    public ResponseEntity<GameRoomRes> gameRoomJoin(@PathVariable String pinNumber,
            @RequestBody(required = false) Map<String, String> param,
            @AuthenticationPrincipal MemberDetail memberDetail,
            HttpServletRequest request) throws OpenViduJavaClientException, OpenViduHttpException {

        String clientIp = ClientUtils.etRemoteAddr(request);
        String nickname = param.get("nickname");
        Long memberId = null;
        if (memberDetail != null) {
            memberId = memberDetail.getId();
        }
        return new ResponseEntity<>(
                gameRoomService.joinGameRoom(pinNumber, memberId, nickname, clientIp),
                HttpStatus.OK);
    }

    @DeleteMapping("/{pinNumber}/sessions/{socketId}")
    @Operation(summary = "게임방 멤버 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방 나가기 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임방")
    })
    public ResponseEntity gameMemberRemoveInGameRoom(@PathVariable String pinNumber,
            @PathVariable String socketId)
        throws OpenViduJavaClientException, OpenViduHttpException {

        gameRoomService.removeGameMember(socketId);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{pinNumber}")
    @Operation(summary = "게임방 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임방")
    })
    public ResponseEntity gameRoomRemove(@PathVariable String pinNumber)
        throws OpenViduJavaClientException, OpenViduHttpException {

        gameRoomService.removeGameRoom(pinNumber);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{pinNumber}")
    @Operation(summary = "게임시작")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게임 시작"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임방")
    })
    public ResponseEntity gameStart(@PathVariable String pinNumber) throws JsonProcessingException {
        gameRoomService.startGame(pinNumber);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/score")
    @Operation(summary = "게임 멤버 이미지 저장")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게임 멤버 이미지 저장성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임방")
    })
    public ResponseEntity userImageScore(@ModelAttribute ScoringReq scoringReq) throws Exception {
        gameRoomService.scoringUserImage(scoringReq);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{pinNumber}/round")
    @Operation(summary = "다음 라운드로 진행")
    public ResponseEntity nextRound(@PathVariable("pinNumber") String pinNumber)
            throws JsonProcessingException {
        gameRoomService.nextRound(pinNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{pinNumber}/final")
    @Operation(summary = "최종 결과 반환")
    public ResponseEntity finalResult(@PathVariable("pinNumber") String pinNumber)
            throws JsonProcessingException {
        gameRoomService.getFinalResult(pinNumber);
        return ResponseEntity.ok().build();
    }
}