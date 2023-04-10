package com.ddockddack.domain.bestcut.controller;

import com.ddockddack.domain.bestcut.request.BestcutSaveReq;
import com.ddockddack.domain.bestcut.response.BestcutRes;
import com.ddockddack.domain.bestcut.service.BestcutLikeService;
import com.ddockddack.domain.bestcut.service.BestcutService;
import com.ddockddack.domain.member.response.MemberAccessRes;
import com.ddockddack.domain.report.entity.ReportType;
import com.ddockddack.global.util.PageConditionReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bestcuts")
public class BestcutApiController {
    private final BestcutService bestcutService;
    private final BestcutLikeService bestcutLikeService;


    @PostMapping
    @Operation(summary = "베스트 컷 게시")
    @ApiResponses({
            @ApiResponse(description = "베스트컷 게시 성공", responseCode = "200"),
            @ApiResponse(description = "필수 값 누락", responseCode = "400"),
    })
    public ResponseEntity bestcutSave(@RequestBody @Valid BestcutSaveReq saveReq,
            Authentication authentication) {
        Long memberId = ((MemberAccessRes)authentication.getPrincipal()).getId();


        bestcutService.saveBestcut(memberId, saveReq);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{bestcutId}")
    @Operation(summary = "베스트 컷 삭제")
    @ApiResponses({
            @ApiResponse(description = "베스트컷 삭제 성공", responseCode = "200"),
            @ApiResponse(description = "권한 없음", responseCode = "401"),
            @ApiResponse(description = "존재하지 않는 베스트컷", responseCode = "404"),
            @ApiResponse(description = "존재하지 않는 멤버", responseCode = "404"),
    })
    public ResponseEntity bestcutDelete(@PathVariable Long bestcutId, Authentication authentication) {
        Long memberId = ((MemberAccessRes)authentication.getPrincipal()).getId();
        bestcutService.removeBestcut(bestcutId, memberId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/like/{bestcutId}")
    @Operation(summary = "베스트 컷 좋아요")
    @ApiResponses({
            @ApiResponse(description = "베스트컷 좋아요 성공", responseCode = "200"),
            @ApiResponse(description = "이미 좋아요한 베스트컷", responseCode = "400"),
            @ApiResponse(description = "존재하지 않는 베스트컷", responseCode = "404"),
            @ApiResponse(description = "존재하지 않는 멤버", responseCode = "404"),
    })
    public ResponseEntity bestcutLike(@PathVariable Long bestcutId, Authentication authentication) {

        Long memberId = ((MemberAccessRes)authentication.getPrincipal()).getId();
        bestcutLikeService.saveBestcutLike(bestcutId, memberId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/dislike/{bestcutId}")
    @Operation(summary = "베스트 컷 좋아요 취소")
    @ApiResponses({
            @ApiResponse(description = "베스트컷 좋아요 취소 성공", responseCode = "200"),
            @ApiResponse(description = "존재하지 않는 베스트컷", responseCode = "404"),
            @ApiResponse(description = "존재하지 않는 멤버", responseCode = "404"),
    })
    public ResponseEntity bestcutDislike(@PathVariable Long bestcutId, Authentication authentication) {

        Long memberId = ((MemberAccessRes)authentication.getPrincipal()).getId();
        bestcutLikeService.removeBestcutLike(bestcutId, memberId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/report/{bestcutId}")
    @Operation(summary = "베스트 컷 신고")
    @ApiResponses({
            @ApiResponse(description = "베스트컷 신고 성공", responseCode = "200"),
            @ApiResponse(description = "이미 신고한 베스트컷", responseCode = "400"),
            @ApiResponse(description = "로그인 필요", responseCode = "401"),
            @ApiResponse(description = "존재하지 않는 베스트컷", responseCode = "404"),
            @ApiResponse(description = "존재하지 않는 멤버", responseCode = "404"),
    })
    public ResponseEntity bescutReport(@PathVariable Long bestcutId,
            @RequestBody Map<String, String> reportType, Authentication authentication) {

        Long memberId = ((MemberAccessRes)authentication.getPrincipal()).getId();
        bestcutService.reportBestcut(memberId, bestcutId,
                ReportType.valueOf(reportType.get("reportType")));

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "베스트 컷 목록 조회")
    @ApiResponses({
            @ApiResponse(description = "베스트컷 조회 성공", responseCode = "200")
    })
    public ResponseEntity<PageImpl<BestcutRes>> bestcutList(Authentication authentication, @ModelAttribute PageConditionReq pageConditionReq) {
        Long memberId = 0L;
        if(authentication!=null){
            memberId = ((MemberAccessRes)authentication.getPrincipal()).getId();
        }

        PageImpl<BestcutRes> result = bestcutService.findAll(false, memberId, pageConditionReq);

        return ResponseEntity.ok(result);
    }

    @GetMapping("{bestcutId}")
    @Operation(summary = "베스트 컷 단건 조회")
    @ApiResponses({
            @ApiResponse(description = "베스트컷 조회 성공", responseCode = "200"),
            @ApiResponse(description = "존재하지 않는 베스트컷", responseCode = "404")
    })
    public ResponseEntity<BestcutRes> bestcutFind(Authentication authentication, @PathVariable Long bestcutId){
        Long memberId = ((MemberAccessRes)authentication.getPrincipal()).getId();
        BestcutRes findBestcut = bestcutService.findOne(memberId, bestcutId);

        return ResponseEntity.ok(findBestcut);
    }
}
