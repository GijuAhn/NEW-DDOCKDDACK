package com.ddockddack.domain.member.controller;


import com.ddockddack.domain.bestcut.response.BestcutRes;
import com.ddockddack.domain.bestcut.service.BestcutService;
import com.ddockddack.domain.multigame.response.MultiGameRes;
import com.ddockddack.domain.multigame.response.StarredGameRes;
import com.ddockddack.domain.multigame.service.MultiGameService;
import com.ddockddack.domain.member.request.MemberModifyNameReq;
import com.ddockddack.domain.member.response.MemberInfoRes;
import com.ddockddack.domain.member.service.MemberService;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.NotFoundException;
import com.ddockddack.global.oauth.MemberDetail;
import com.ddockddack.domain.multigame.request.paging.PageConditionReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "member", description = "member API 입니다.")
@RequestMapping("/members")
public class MemberApiController {

    private final MemberService memberService;
    private final BestcutService bestcutService;
    private final MultiGameService multiGameService;

    @Operation(summary = "회원 nickname 수정", description = "회원 nickname 수정 메소드입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이력 조회 성공"),
        @ApiResponse(responseCode = "400", description = "필수 값 누락"),
        @ApiResponse(responseCode = "400", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/nickname")
    public ResponseEntity nicknameModify(@RequestBody MemberModifyNameReq
        memberModifyNameReq,
        @AuthenticationPrincipal MemberDetail memberDetail) {

        memberService.modifyMemberNickname(memberDetail.getId(), memberModifyNameReq);

        return ResponseEntity.ok().build();

    }

    @Operation(summary = "회원 이미지 수정", description = "회원 이미지 수정 메소드입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이력 조회 성공"),
        @ApiResponse(responseCode = "400", description = "필수 값 누락"),
        @ApiResponse(responseCode = "400", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
        @ApiResponse(responseCode = "413", description = "파일용량 초과"),
        @ApiResponse(responseCode = "415", description = "지원하지않는 확장자"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/profile")
    public ResponseEntity profileModify(
        @ModelAttribute MultipartFile profileImg,
        @AuthenticationPrincipal MemberDetail memberDetail
    ) {
        String imageName = memberService.modifyMemberProfile(memberDetail.getId(), profileImg);

        return ResponseEntity.ok().body(imageName);

    }

    @Operation(summary = "내 정보 조회", description = "회원 정보 조회 메소드입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이력 조회 성공"),
        @ApiResponse(responseCode = "400", description = "파라미터 타입 오류"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping()
    public ResponseEntity memberDetails(@AuthenticationPrincipal MemberDetail memberDetail) {

        if (memberDetail.toString().equals("anonymousUser")) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }
        MemberInfoRes memberInfoRes = memberService.memberDetails(memberDetail.getId());

        return ResponseEntity.ok(memberInfoRes);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 메소드입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이력 조회 성공"),
        @ApiResponse(responseCode = "400", description = "파라미터 타입 오류"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping()
    public ResponseEntity memberRemove(HttpServletRequest request, HttpServletResponse response,
        @AuthenticationPrincipal MemberDetail memberDetail) {

        removeCookie(request.getCookies(), response);

        memberService.removeMemberById(
            memberDetail.getId()); //탈퇴로직에 access, refresh Token 정지시키는 로직 추가해야함

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 베스트 컷 전체 조회", description = "내 베스트 컷 전체 조회 메소드입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "내 베스트 컷 전체 조회 성공"),
        @ApiResponse(responseCode = "400", description = "파라미터 타입 오류"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/bestcuts")
    public ResponseEntity myBestcutList(
        @ModelAttribute PageConditionReq pageCondition,
        @AuthenticationPrincipal MemberDetail memberDetail) {
        PageImpl<BestcutRes> bestcutRes = bestcutService.findAllBestcuts(true, memberDetail.getId(),
            pageCondition);
        return ResponseEntity.ok(bestcutRes);

    }

    @Operation(summary = "내가 만든 게임 전체 조회", description = "내가 만든 게임 전체 조회 메소드입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "내가 만든 게임 전체 조회 성공"),
        @ApiResponse(responseCode = "400", description = "파라미터 타입 오류"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/games")
    public ResponseEntity myGameList(
        @ModelAttribute PageConditionReq pageConditionReq,
        @AuthenticationPrincipal MemberDetail memberDetail) {
        PageImpl<MultiGameRes> gameResList = multiGameService.findAllGamesByMemberId(memberDetail.getId(),
            pageConditionReq);
        return ResponseEntity.ok(gameResList);
    }

    @Operation(summary = "즐겨찾기 게임 전체 조회", description = "내가 만든 게임 전체 조회 메소드입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "즐겨찾기 게임 전체 조회 성공"),
        @ApiResponse(responseCode = "400", description = "파라미터 타입 오류"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/starred")
    public ResponseEntity StarredGameList(@AuthenticationPrincipal MemberDetail memberDetail) {
        List<StarredGameRes> starredGameResList = multiGameService.findAllStarredGames(
            memberDetail.getId());
        return ResponseEntity.ok(starredGameResList);

    }

    @Operation(summary = "로그아웃", description = "로그아웃 메소드입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "사용자 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/logout")
    public ResponseEntity
    logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = removeCookie(request.getCookies(), response);
        memberService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }

    private String removeCookie(Cookie[] cookies, HttpServletResponse response) {
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh-token")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        Cookie refreshTokenCookie = new Cookie("refresh-token", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        return refreshToken;
    }

}


