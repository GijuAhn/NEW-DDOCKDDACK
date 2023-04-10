package com.ddockddack.domain.member.controller;


import com.ddockddack.domain.bestcut.response.BestcutRes;
import com.ddockddack.domain.bestcut.service.BestcutService;
import com.ddockddack.domain.game.response.GameRes;
import com.ddockddack.domain.game.response.StarredGameRes;
import com.ddockddack.domain.game.service.GameService;
import com.ddockddack.domain.gameRoom.response.GameRoomHistoryRes;
import com.ddockddack.domain.gameRoom.service.GameRoomService;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.request.MemberModifyNameReq;
import com.ddockddack.domain.member.response.MemberAccessRes;
import com.ddockddack.domain.member.service.MemberService;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AccessDeniedException;
import com.ddockddack.global.error.exception.NotFoundException;
import com.ddockddack.global.util.PageConditionReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "member", description = "member API 입니다.")
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;
    private final BestcutService bestcutService;
    private final GameService gameService;
    private final GameRoomService gameRoomService;

    @Operation(summary = "회원 nickname 수정", description = "회원 nickname 수정 메소드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이력 조회 성공"),
            @ApiResponse(responseCode = "400", description = "필수 값 누락"),
            @ApiResponse(responseCode = "400", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/nickname")
    public ResponseEntity modifyMemberNickname(@RequestBody MemberModifyNameReq
                                                       memberModifyNameReq) {
        try {
            MemberAccessRes memberAccessRes = (MemberAccessRes) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            Optional<Member> member = memberService.getMemberById(memberAccessRes.getId());
            if (member.isEmpty()) {
                throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
            }
            memberService.modifyMemberNickname(member.get().getId(), memberModifyNameReq);

//            return ResponseEntity.ok(memberService.modifyMember(member.get().getId(), modifyMemberReq));
            log.info("memberModifyNameReq {}", memberModifyNameReq);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
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
    public ResponseEntity modifyMemberProfileImg(
            @ModelAttribute MultipartFile profileImg
    ) {
        try {
            MemberAccessRes memberAccessRes = (MemberAccessRes) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            Optional<Member> member = memberService.getMemberById(memberAccessRes.getId());
            if (member.isEmpty()) {
                throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
            }
            if (profileImg.isEmpty()) {
                throw new NotFoundException(ErrorCode.MISSING_REQUIRED_VALUE);
            }

            log.info("profileImg {}", profileImg.getOriginalFilename());

            String imageName = memberService.modifyMemberProfileImg(member.get().getId(), profileImg);

            return ResponseEntity.ok().body(imageName);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @Operation(summary = "내 정보 조회", description = "회원 정보 조회 메소드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이력 조회 성공"),
            @ApiResponse(responseCode = "400", description = "파라미터 타입 오류"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping()
    public ResponseEntity getMemberInfo() {
        log.info("sec info {}",
                SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        MemberAccessRes memberAccessRes = Optional.ofNullable(
                (MemberAccessRes) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).get();
        if (memberAccessRes.toString().equals("anonymousUser")) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }

        Optional<Member> member = memberService.getMemberById(memberAccessRes.getId());
        log.info("member {}", member.get());
        if (member.isEmpty()) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return ResponseEntity.ok(member.get());
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 메소드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이력 조회 성공"),
            @ApiResponse(responseCode = "400", description = "파라미터 타입 오류"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping()
    public ResponseEntity deleteMember(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info(String.valueOf(cookie.getName()));
                if (cookie.getName().equals("refresh-token")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        MemberAccessRes memberAccessRes = Optional.ofNullable(
                (MemberAccessRes) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).get();
        memberService.deleteMemberById(
                memberAccessRes.getId()); //탈퇴로직에 access, refresh Token 정지시키는 로직 추가해야함

        log.info("withdrawal {} ", refreshToken);
        Cookie refreshTokenCookie = new Cookie("refresh-token", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

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
    public ResponseEntity getBestcuts(
        @ModelAttribute PageConditionReq pageCondition, Authentication authentication) {
        Long memberId = ((MemberAccessRes)authentication.getPrincipal()).getId();
        try {
            log.info("bestcuts {}, {}", memberId, pageCondition.toString());

            PageImpl<BestcutRes> bestcutRes = bestcutService.findAll(true, memberId, pageCondition);
            log.info("bestcuts2 {}", bestcutRes);
            return ResponseEntity.ok(bestcutRes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }
    }

    @Operation(summary = "내가 만든 게임 전체 조회", description = "내가 만든 게임 전체 조회 메소드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 만든 게임 전체 조회 성공"),
            @ApiResponse(responseCode = "400", description = "파라미터 타입 오류"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/games")
    public ResponseEntity getMyGames(
        @ModelAttribute PageConditionReq pageConditionReq, Authentication authentication) {
        Long memberId = ((MemberAccessRes)authentication.getPrincipal()).getId();
        try {
            PageImpl<GameRes> gameResList = gameService.findAllGameByMemberId(memberId, pageConditionReq);
            return ResponseEntity.ok(gameResList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }
    }

    @Operation(summary = "즐겨찾기 게임 전체 조회", description = "내가 만든 게임 전체 조회 메소드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "즐겨찾기 게임 전체 조회 성공"),
            @ApiResponse(responseCode = "400", description = "파라미터 타입 오류"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/starred")
    public ResponseEntity getStarredGames(Authentication authentication) {
        Long memberId = ((MemberAccessRes)authentication.getPrincipal()).getId();
        try {
            List<StarredGameRes> starredGameResList = gameService.findAllStarredGames(
                    memberId);
            return ResponseEntity.ok(starredGameResList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }
    }

    @Operation(summary = "게임 이력 조회", description = "게임 이력 조회 메소드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이력 조회 성공"),
            @ApiResponse(responseCode = "400", description = "파라미터 타입 오류"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/records")
    public ResponseEntity getRecords(Authentication authentication) {
        Long memberId = ((MemberAccessRes)authentication.getPrincipal()).getId();
        try {
            List<GameRoomHistoryRes> roomHistory = gameRoomService.findAllRoomHistory(memberId);
            Collections.reverse(roomHistory);
            return ResponseEntity.ok(roomHistory);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);
        }
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
    logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info(String.valueOf(cookie.getName()));
                if (cookie.getName().equals("refresh-token")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        log.info("logout {} ", refreshToken);
        Cookie refreshTokenCookie = new Cookie("refresh-token", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
        memberService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }

    private void checkLogin(String accessToken) {
        if (accessToken == null) {
            throw new AccessDeniedException(ErrorCode.LOGIN_REQUIRED); //권한이 없는 데이터 접속
        }
    }

    private void unlinkKakao() {
        //                HttpHeaders tokenHeaders = new HttpHeaders(); //로그아웃하면 accessToken 연결 끊기
//                tokenHeaders.add("Authorization", "Bearer " + accessToken);
//                tokenHeaders.add("Content-type", "application/x-www-form-urlencoded");
//
//                HttpEntity<MultiValueMap<String, String>> memberUnlinkReq = new HttpEntity<>(tokenHeaders);
//
//                RestTemplate rt = new RestTemplate();
//
//                ResponseEntity<String> memberUnlinkRes = rt.exchange(
//                        "https://kapi.kakao.com/v1/member/unlink",
//                        HttpMethod.POST,
//                        memSberUnlinkReq,
//                        String.class
//                );

//                System.out.println("=============================");
//                System.out.println("유저 해제" + memberUnlinkRes);
    }


    /*@Operation(summary = "카카오 로그인", description = "카카오 로그인 메소드입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "사용자 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/kakaologin")
    public ResponseEntity<?> kakaoRequestAccessToken(@RequestParam String code) {
        //카카오 서버에 POST 방식으로 엑세스 토큰을 요청
        //RestTemplate를 이용
        try {
            System.out.println(code);
            String accessToken = memberService.getKaKaoAccessToken(code);

            ResponseEntity<String> memberInfoResponse = memberService.getKakaoMember(accessToken);

            JsonParser jp = new JsonParser();
//            JsonObject jo = jp.parse(memberInfoResponse.getBody()).getAsJsonObject();

            JsonObject memberJsonObject = jp.parse(memberInfoResponse.getBody()).getAsJsonObject();
            JsonObject memberAccountObject = jp.parse(
                memberJsonObject.get("kakao_account").toString()).getAsJsonObject();
            String nickname = jp.parse(memberAccountObject.get("profile").toString())
                .getAsJsonObject().get("nickname").getAsString();
            String email = memberAccountObject.get("email").getAsString();

            boolean isMember = memberService.findUserBySocialId(email);

            if (!isMember) {
                Member member = new Member(email, nickname, "", Role.MEMBER);
                MemberInfoRes memberInfoRes = new MemberInfoRes(member.getNickname(),
                    member.getProfile());

                memberService.joinMember(member);

                return ResponseEntity.ok(memberInfoRes);
            }
            //else
            return ResponseEntity.ok(memberService.getMemberByEmail(email));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(500).body(e);
        }

    }

    @Operation(summary = "구글 로그인", description = "구글 로그인 메소드입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "사용자 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/googlelogin")
    public ResponseEntity<?> GoogleRequestAccessToken(@RequestParam String code) {
        try {
            String accessToken = memberService.getGoogleAccessToken(code);

            ResponseEntity<String> memberInfoResponse = memberService.getGoogleMember(accessToken);

            JsonParser jp = new JsonParser();

            JsonObject memberJsonObject = jp.parse(memberInfoResponse.getBody()).getAsJsonObject();

            String email = memberJsonObject.get("email").getAsString();
            boolean isMember = memberService.findUserBySocialId(email);

            if (!isMember) {
                Member member = new Member(email, "", "", Role.MEMBER);
                MemberInfoRes memberInfoRes = new MemberInfoRes(member.getNickname(),
                    member.getProfile());

                memberService.joinMember(member);

                return ResponseEntity.ok(memberInfoRes);
            }
            //else
            return ResponseEntity.ok(memberService.getMemberByEmail(email));

        } catch (Exception e) {
            throw new AccessDeniedException(ErrorCode.NOT_AUTHORIZED);
        }
    }*/

}


