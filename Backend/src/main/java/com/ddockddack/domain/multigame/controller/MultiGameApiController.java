package com.ddockddack.domain.multigame.controller;

import com.ddockddack.domain.multigame.request.GameModifyReq;
import com.ddockddack.domain.multigame.request.GameSaveReq;
import com.ddockddack.domain.multigame.response.MultiGameDetailRes;
import com.ddockddack.domain.multigame.response.MultiGameRes;
import com.ddockddack.domain.multigame.service.MultiGameService;
import com.ddockddack.domain.report.entity.ReportType;
import com.ddockddack.global.oauth.MemberDetail;
import com.ddockddack.domain.multigame.request.paging.PageConditionReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/multi-games")
public class MultiGameApiController {

    private final MultiGameService multiGameService;

    @GetMapping()
    @Operation(summary = "게임 목록 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 목록 조회 성공")
    })
    public ResponseEntity<PageImpl<MultiGameRes>> gameList(
        @ModelAttribute PageConditionReq pageConditionReq,
        @AuthenticationPrincipal MemberDetail memberDetail) {
        Long memberId = null;
        if (memberDetail != null) {
            memberId = memberDetail.getId();
        }
        PageImpl<MultiGameRes> allGames = multiGameService.findAllGames(memberId, pageConditionReq);

        return ResponseEntity.ok(allGames);
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "게임 상세조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임")
    })
    public ResponseEntity<MultiGameDetailRes> gameDetails(@PathVariable Long gameId) {

        return ResponseEntity.ok(multiGameService.findGame(gameId));
    }

    @PostMapping
    @Operation(summary = "게임 생성")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 생성 성공"),
        @ApiResponse(responseCode = "400", description = "필수 값 누락"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저"),
        @ApiResponse(responseCode = "413", description = "파일 용량 초과"),
        @ApiResponse(responseCode = "414", description = "입력 범위 벗어남"),
        @ApiResponse(responseCode = "414", description = "지원 하지 않는 확장자")
    })
    public ResponseEntity gameSave(@ModelAttribute @Valid GameSaveReq gameSaveReq,
        @AuthenticationPrincipal MemberDetail memberDetail) {

        multiGameService.saveGame(memberDetail.getId(), gameSaveReq);
        return ResponseEntity.ok().build();

    }

    @PutMapping("/{gameId}")
    @Operation(summary = "게임 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 수정 성공"),
        @ApiResponse(responseCode = "400", description = "필수 값 누락"),
        @ApiResponse(responseCode = "401", description = "권한 없음"),
        @ApiResponse(responseCode = "413", description = "파일 용량 초과"),
        @ApiResponse(responseCode = "414", description = "지원 하지 않는 확장자")
    })
    public ResponseEntity gameModify(@ModelAttribute @Valid GameModifyReq gameModifyReq,
        @AuthenticationPrincipal MemberDetail memberDetail) {

        multiGameService.modifyGame(memberDetail, gameModifyReq);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{gameId}")
    @Operation(summary = "게임 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저")
    })
    public ResponseEntity gameRemove(@PathVariable Long gameId,
        @AuthenticationPrincipal MemberDetail memberDetail) {

        multiGameService.removeGame(memberDetail, gameId);

        return ResponseEntity.ok().build();

    }

    @PostMapping("/starred/{gameId}")
    @Operation(summary = "게임 즐겨 찾기")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 즐겨찾기 성공"),
        @ApiResponse(responseCode = "401", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저")
    })
    public ResponseEntity gameStarred(@PathVariable Long gameId,
        @AuthenticationPrincipal MemberDetail memberDetail) {
        multiGameService.starredGame(memberDetail.getId(), gameId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unstarred/{gameId}")
    @Operation(summary = "게임 즐겨 찾기 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 즐겨찾기 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저")
    })
    public ResponseEntity gameUnStarred(@PathVariable Long gameId,
        @AuthenticationPrincipal MemberDetail memberDetail) {
        multiGameService.unStarredGame(memberDetail.getId(), gameId);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/report/{gameId}")
    @Operation(summary = "게임 신고")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 신고 성공"),
        @ApiResponse(responseCode = "400", description = "이미 존재하는 신고"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저")
    })
    public ResponseEntity gameReport(@PathVariable Long gameId,
        @RequestBody Map<String, String> body,
        @AuthenticationPrincipal MemberDetail memberDetail) {
        multiGameService.reportGame(memberDetail.getId(), gameId,
            ReportType.valueOf(body.get("reportType")));
        return ResponseEntity.ok().build();

    }

}
