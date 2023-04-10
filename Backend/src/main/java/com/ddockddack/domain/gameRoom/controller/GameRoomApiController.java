package com.ddockddack.domain.gameRoom.controller;

import com.ddockddack.domain.gameRoom.repository.GameRoom;
import com.ddockddack.domain.gameRoom.response.GameMemberRes;
import com.ddockddack.domain.gameRoom.response.GameRoomRes;
import com.ddockddack.domain.gameRoom.service.GameRoomService;
import com.ddockddack.domain.member.response.MemberAccessRes;
import com.ddockddack.domain.member.service.TokenService;
import com.ddockddack.global.util.ClientUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game-rooms")
public class GameRoomApiController {

    private final GameRoomService gameRoomService;
    private final TokenService tokenService;

    @PostMapping
    @Operation(summary = "게임방 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방 생성 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임")
    })
    public ResponseEntity<String> createRoom(@RequestBody Map<String, String> params) {
        String pinNumber;
        try {
            pinNumber = gameRoomService.createRoom(Long.parseLong(params.get("gameId")));
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
    public ResponseEntity<GameRoomRes> joinRoom(@PathVariable String pinNumber,
                                                @RequestBody(required = false) Map<String, String> param,
                                                Authentication authentication,
                                                HttpServletRequest request) throws OpenViduJavaClientException, OpenViduHttpException {

        String clientIp = ClientUtils.etRemoteAddr(request);
        log.info("clientIp 확인 {}", clientIp);
        String nickname = param.get("nickname");
        Long memberId = null;
        if(authentication != null) {
            memberId = ((MemberAccessRes) authentication.getPrincipal()).getId();
        }
        return new ResponseEntity<>(gameRoomService.joinRoom(pinNumber, memberId, nickname, clientIp), HttpStatus.OK);
    }

    @DeleteMapping("/{pinNumber}/sessions/{socketId}")
    @Operation(summary = "게임방 멤버 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방 나가기 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임방")
    })
    public ResponseEntity removeGameMember(@PathVariable String pinNumber,
                                           @PathVariable String socketId) {


        gameRoomService.removeGameMember(pinNumber, socketId);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{pinNumber}")
    @Operation(summary = "게임방 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임방")
    })
    public ResponseEntity removeGameRoom(@PathVariable String pinNumber) {

        gameRoomService.removeGameRoom(pinNumber);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{pinNumber}")
    @Operation(summary = "게임시작")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게임 시작"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임방")
    })
    public ResponseEntity startGame(@PathVariable String pinNumber) throws JsonProcessingException {
        gameRoomService.startGame(pinNumber);
        final GameRoom gameRoom = gameRoomService.findGameRoom(pinNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pinNumber}/isstarted")
    @Operation(summary = "게임 시작여부 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게임 시작여부 조회성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임방")
    })
    public ResponseEntity<Boolean> getIsStartedGame(@PathVariable String pinNumber) {
        System.out.println("여기불리언");
        System.out.println(gameRoomService.isStartedGame(pinNumber));
        return ResponseEntity.ok(gameRoomService.isStartedGame(pinNumber));
    }

    @PostMapping("/{pinNumber}/{socketId}/images")
    @Operation(summary = "게임 멤버 이미지 저장")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게임 멤버 이미지 저장성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임방")
    })
    public ResponseEntity scoringImage(@PathVariable("pinNumber") String pinNumber,
                                       @PathVariable("socketId") String socketId,
                                       @RequestBody HashMap<String, String> param) throws Exception {
        gameRoomService.scoringImage(pinNumber, socketId, param);

        return ResponseEntity.ok().build();
    }

    @GetMapping("{pinNumber}/result/{round}")
    @Operation(summary = "게임 라운드 결과 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "라운드 결과 반환 성공")
    })
    public ResponseEntity<List<GameMemberRes>> getResult(@PathVariable("pinNumber") String pinNumber,
                                                         @PathVariable("round") int round) {

        return ResponseEntity.ok(gameRoomService.findRoundResult(pinNumber, round));
    }

    @GetMapping("{pinNumber}/round")
    @Operation(summary = "다음 라운드로 진행")
    public ResponseEntity next(@PathVariable("pinNumber") String pinNumber) throws JsonProcessingException {
        gameRoomService.nextRound(pinNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{pinNumber}/final")
    @Operation(summary = "최종 결과 반환")
    public ResponseEntity finalResult(@PathVariable("pinNumber") String pinNumber) throws JsonProcessingException {
        gameRoomService.finalResult(pinNumber);
        return ResponseEntity.ok().build();
    }


}