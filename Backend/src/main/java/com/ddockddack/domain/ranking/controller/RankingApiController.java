package com.ddockddack.domain.ranking.controller;

import com.ddockddack.domain.ranking.request.RankingSaveReq;
import com.ddockddack.domain.ranking.response.RankingRes;
import com.ddockddack.domain.ranking.service.RankingService;
import com.ddockddack.global.oauth.MemberDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ranks")
public class RankingApiController {

    private final RankingService rankingService;

    @GetMapping("/{gameId}")
    @Operation(summary = "랭킹 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "랭킹 조회 성공")
    })
    public ResponseEntity<List<RankingRes>> rankingList(@PathVariable Long gameId) {
        return ResponseEntity.ok(rankingService.getRanking(gameId));
    }

    @PostMapping
    @Operation(summary = "랭킹 등록")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "랭킹 등록 성공")
    })
    public ResponseEntity rankingSave(@ModelAttribute @Valid RankingSaveReq rankingSaveReq, @AuthenticationPrincipal MemberDetail memberDetail) {
        rankingService.saveRanking(rankingSaveReq, memberDetail.getId());
        return ResponseEntity.ok().build();
    }
}