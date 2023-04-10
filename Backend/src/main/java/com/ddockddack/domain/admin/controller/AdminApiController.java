package com.ddockddack.domain.admin.controller;

import com.ddockddack.domain.admin.service.AdminService;
import com.ddockddack.domain.bestcut.repository.BestcutRepositorySupport;
import com.ddockddack.domain.bestcut.response.ReportedBestcutRes;
import com.ddockddack.domain.bestcut.service.BestcutService;
import com.ddockddack.domain.game.response.ReportedGameRes;
import com.ddockddack.domain.game.service.GameService;
import com.ddockddack.domain.member.service.BanLevel;
import com.ddockddack.domain.member.service.MemberService;
import com.ddockddack.domain.member.service.TokenService;
import com.ddockddack.domain.report.service.ReportService;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final BestcutService bestcutService;
    private final GameService gameService;
    private final AdminService adminService;
    private final TokenService tokenService;
    private final MemberService memberService;
    private final BestcutRepositorySupport bestcutRepositorySupport;
    private final ReportService reportService;

    @JsonCreator
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
    public ResponseEntity<?> reportedGameList(@RequestHeader(value = "access-token", required = true) String accessToken) {
        System.out.println(accessToken);
        try {
            List<ReportedGameRes> allReportedGames = gameService.findAllReportedGames();
            return ResponseEntity.ok(allReportedGames);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }

    }

    @GetMapping("/reported/bestcuts")
    @Operation(summary = "신고된 베스트 컷 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고된 베스트 컷 목록 조회 조회 성공"),
            @ApiResponse(responseCode = "403", description = "허가되지 않은 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> reportedBestCutList(@RequestHeader(value = "access-token", required = true) String accessToken) {

        try {
            List<ReportedBestcutRes> allReportedBestCuts = bestcutService.findAllReportedBestCuts();
            return ResponseEntity.ok(allReportedBestCuts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }

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
    public ResponseEntity reportedGameDelete(@PathVariable Long gameId, @PathVariable Long reportId,
                                             @RequestHeader(value = "access-token", required = true) String accessToken,
                                             @RequestHeader(value = "banMemberId", required = true) Long banMemberId,
                                             @RequestHeader(value = "banLevel", required = true) String banLevel) {

        Long adminId = tokenService.getUid(accessToken);

        gameService.removeGame(adminId, gameId);
        if (stringToEnum(banLevel) != BanLevel.noPenalty) memberService.banMember(banMemberId, stringToEnum(banLevel));

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
    public ResponseEntity gameReportDelete(@PathVariable Long reportId,
                                           @RequestHeader(value = "access-token", required = true) String accessToken) {

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
    public ResponseEntity reportedBestCutDelete(@PathVariable Long bestcutId, @PathVariable Long reportId,
                                                @RequestHeader(value = "access-token", required = true) String accessToken,
                                                @RequestHeader(value = "banMemberId", required = true) Long banMemberId,
                                                @RequestHeader(value = "banLevel", required = true) String banLevel) {
        Long adminId = tokenService.getUid(accessToken);

        bestcutService.removeBestcut(bestcutId, adminId);
        if (stringToEnum(banLevel) != BanLevel.noPenalty) memberService.banMember(banMemberId, stringToEnum(banLevel));

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
    public ResponseEntity BestCutReportDelete(@PathVariable Long reportId,
                                              @RequestHeader(value = "access-token", required = true) String accessToken) {

        bestcutRepositorySupport.removeReportedBestcut(reportId);

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
    public ResponseEntity<?> banMember(@PathVariable Long banMemberId,
                                       @RequestHeader(value = "access-token", required = true) String accessToken,
                                       @RequestBody Map<String, Object> Data) {

        BanLevel banLevel = stringToEnum((String) Data.get("banLevel"));

        try {
            return ResponseEntity.ok(memberService.banMember(banMemberId, banLevel));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
