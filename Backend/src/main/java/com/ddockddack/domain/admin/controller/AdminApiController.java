package com.ddockddack.domain.admin.controller;

import com.ddockddack.domain.bestcut.response.ReportedBestcutRes;
import com.ddockddack.domain.bestcut.service.BestcutService;
import com.ddockddack.domain.game.response.ReportedGameRes;
import com.ddockddack.domain.game.service.GameService;
import com.ddockddack.domain.member.response.MemberAccessRes;
import com.ddockddack.domain.member.service.BanLevel;
import com.ddockddack.domain.member.service.MemberService;
import com.ddockddack.domain.report.service.ReportService;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final BestcutService bestcutService;
    private final GameService gameService;
    private final MemberService memberService;
    private final ReportService reportService;

    public static BanLevel stringToEnum(String input) {
        return BanLevel.valueOf(input);
    }

    @GetMapping("/reported/games")
    @Operation(summary = "신고된 게임 목록 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "신고된 게임 목록 조회 성공"),
        @ApiResponse(responseCode = "403", description = "허가되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<ReportedGameRes>> reportedGameList() {

        return ResponseEntity.ok(gameService.findAllReportedGames());
    }

    @GetMapping("/reported/bestcuts")
    @Operation(summary = "신고된 베스트 컷 목록 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "신고된 베스트 컷 목록 조회 조회 성공"),
        @ApiResponse(responseCode = "403", description = "허가되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<ReportedBestcutRes>> reportedBestCutList() {

        return ResponseEntity.ok(bestcutService.findAllReportedBestCuts());

    }

    @DeleteMapping("/remove/game/{reportId}/{gameId}")
    @Operation(summary = "신고된 게임 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "권한 없음"),
        @ApiResponse(responseCode = "403", description = "허가되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저")
    })
    public ResponseEntity reportedGameRemove(@PathVariable Long gameId,
        @RequestHeader(value = "banMemberId", required = true) Long banMemberId,
        @RequestHeader(value = "banLevel", required = true) String banLevel,
        Authentication authentication) {

        Long adminId = ((MemberAccessRes) authentication.getPrincipal()).getId();

        gameService.removeGame(adminId, gameId);
        if (stringToEnum(banLevel) != BanLevel.NO_PENALTY) {
            memberService.banMember(banMemberId, stringToEnum(banLevel));
        }

        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/remove/game/{reportId}")
    @Operation(summary = "게임 신고 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 신고 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "권한 없음"),
        @ApiResponse(responseCode = "403", description = "허가되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 신고"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저")
    })
    public ResponseEntity gameReportRemove(@PathVariable Long reportId) {

        reportService.removeReportedGame(reportId);

        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/remove/bestcut/{reportId}/{bestcutId}")
    @Operation(summary = "신고된 베스트 컷 삭제")
    @ApiResponses({
        @ApiResponse(description = "베스트컷 삭제 성공", responseCode = "200"),
        @ApiResponse(description = "권한 없음", responseCode = "401"),
        @ApiResponse(responseCode = "403", description = "허가되지 않은 사용자"),
        @ApiResponse(description = "존재하지 않는 베스트컷", responseCode = "404"),
        @ApiResponse(description = "존재하지 않는 멤버", responseCode = "404"),
    })
    public ResponseEntity bestcutRemove(@PathVariable Long bestcutId,
        @RequestHeader(value = "banMemberId", required = true) Long banMemberId,
        @RequestHeader(value = "banLevel", required = true) String banLevel,
        Authentication authentication) {
        Long adminId = ((MemberAccessRes) authentication.getPrincipal()).getId();

        bestcutService.removeBestcut(bestcutId, adminId);
        if (stringToEnum(banLevel) != BanLevel.NO_PENALTY) {
            memberService.banMember(banMemberId, stringToEnum(banLevel));
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/bestcut/{reportId}")
    @Operation(summary = "베스트 컷 신고 삭제")
    @ApiResponses({
        @ApiResponse(description = "베스트컷 신고 삭제 성공", responseCode = "200"),
        @ApiResponse(description = "권한 없음", responseCode = "401"),
        @ApiResponse(responseCode = "403", description = "허가되지 않은 사용자"),
        @ApiResponse(description = "존재하지 않는 신고", responseCode = "404"),
        @ApiResponse(description = "존재하지 않는 멤버", responseCode = "404"),
    })
    public ResponseEntity reportedBestcutRemove(@PathVariable Long reportId) {

        reportService.removeReportedBestcut(reportId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 정지", description = "회원 정지 메소드입니다.")
    @PutMapping("/ban/{banMemberId}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이력 조회 성공"),
        @ApiResponse(responseCode = "403", description = "허가되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity memberBan(@PathVariable Long banMemberId,
        @RequestBody Map<String, Object> data) {
        BanLevel banLevel = stringToEnum((String) data.get("banLevel"));

        return ResponseEntity.ok(memberService.banMember(banMemberId, banLevel));

    }
}
