package com.ddockddack.domain.singlegame.controller;

import com.ddockddack.domain.singlegame.request.FaceSimilarityReq;
import com.ddockddack.domain.singlegame.response.SingleGameRes;
import com.ddockddack.domain.singlegame.service.SingleGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/single-games")
public class SingleGameApiController {

    private final SingleGameService singleGameService;

    @GetMapping()
    @Operation(summary = "싱글 게임 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게임 목록 조회 성공")
    })
    public ResponseEntity<PageImpl<SingleGameRes>> gameList(@RequestParam String keyword,
            @PageableDefault(size = 9, sort = "id", direction = Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(singleGameService.getSingleGameList(keyword, pageable));
    }

    @PostMapping("/score")
    @Operation(summary = "얼굴 비교")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "얼굴 유사도 비교 성공")
    })
    public ResponseEntity<Float> faceSimilarity(@ModelAttribute FaceSimilarityReq faceSimilarityReq) throws IOException {
        return ResponseEntity.ok(singleGameService.getScore(faceSimilarityReq));
    }

}
