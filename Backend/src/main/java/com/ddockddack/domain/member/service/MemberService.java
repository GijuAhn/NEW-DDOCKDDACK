package com.ddockddack.domain.member.service;

import com.ddockddack.domain.bestcut.service.BestcutService;
import com.ddockddack.domain.game.repository.GameImageRepository;
import com.ddockddack.domain.game.repository.GameRepository;
import com.ddockddack.domain.game.repository.GameRepositorySupport;
import com.ddockddack.domain.game.repository.StarredGameRepository;
import com.ddockddack.domain.gameRoom.repository.GameRoomHistoryRepository;
import com.ddockddack.domain.gameRoom.repository.GameRoomHistoryRepositorySupport;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.entity.Role;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.member.request.MemberModifyNameReq;
import com.ddockddack.domain.report.repository.ReportedGameRepository;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.ImageExtensionException;
import com.ddockddack.global.error.exception.NotFoundException;
import com.ddockddack.global.service.AwsS3Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final Environment env;
    private final MemberRepository memberRepository;
    private final BestcutService bestcutService;
    private final TokenService tokenService;
    private final ReportedGameRepository reportedGameRepository;
    private final GameImageRepository gameImageRepository;
    private final StarredGameRepository starredGameRepository;
    private final GameRepositorySupport gameRepositorySupport;
    private final GameRepository gameRepository;
    private final GameRoomHistoryRepository gameRoomHistoryRepository;
    private final GameRoomHistoryRepositorySupport gameRoomHistoryRepositorySupport;
    private final AwsS3Service awsS3Service;


    //    private final RedisTemplate redisTemplate;
    private RestTemplate rt;

//    /**
//     * @param memberId
//     * @param modifyMember
//     * @return 수정된 member
//     */
//    @Transactional
//    public Member modifyMember(Long memberId, MemberModifyReq modifyMember) {
//        Member memberToModify = memberRepository.findById(memberId).get();
//        if (!memberToModify.getNickname().equals(modifyMember.getNickname())) {
//            memberToModify.setNickname(modifyMember.getNickname());
//        }
//        log.info("log! {}, {}", modifyMember.getProfile(), modifyMember.getProfile().isEmpty());
//        if (!memberToModify.getProfile().equals(modifyMember.getProfile())) {
//            memberToModify.setProfile(modifyMember.getProfile());
//        }
//
//        return null;
////        return memberRepository.save(memberToModify);
//    }

    @Transactional
    public void modifyMemberNickname(Long memberId, MemberModifyNameReq modifyMemberNickname) {
        Member member = memberRepository.findById(memberId).get();
        log.info("log! {}, {}", modifyMemberNickname.getNickname(),
            modifyMemberNickname.getNickname().isEmpty());
        if (!member.getNickname().equals(modifyMemberNickname.getNickname())) {
            member.modifyNickname(modifyMemberNickname.getNickname());
        }
//        return memberRepository.save(member);
    }

    @Transactional
    public String modifyMemberProfileImg(Long memberId, MultipartFile modifyProfileImg) {
        Member member = memberRepository.findById(memberId).get();

        if (!(modifyProfileImg.getContentType().contains("image/jpg") ||
            (modifyProfileImg.getContentType().contains("image/jpeg") ||
                (modifyProfileImg.getContentType().contains("image/png"))))) {
            throw new ImageExtensionException(ErrorCode.EXTENSION_NOT_ALLOWED);
        }

        log.info("modifyProfileImg contentType {}", modifyProfileImg.getContentType());

        try {
            String fileName = awsS3Service.multipartFileUpload(modifyProfileImg);
            if (!member.getProfile().equals("default_profile_img.png")) {
                awsS3Service.deleteObject(member.getProfile());
            }
            member.modifyProfile(fileName);

            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("UPLOAD_FAILED"); //Exception 추가
        }

    }

    /**
     * @param memberId
     * @return member 정보
     */
    public Optional<Member> getMemberById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.getByEmail(email);
    }

    @Transactional
    public void deleteMemberById(Long memberId) {
        List<Long> bestcutIds = bestcutService.findByMemberId(memberId);
        bestcutService.removeByMemberId(memberId);
        bestcutService.removeAllByIds(bestcutIds);

        List<Long> gameIds = gameRepositorySupport.findAllGameIdByMemberId(memberId);
        gameImageRepository.deleteAllByGameId(gameIds);
        starredGameRepository.deleteByMemberId(memberId);
        starredGameRepository.deleteAllByGameId(gameIds);
        reportedGameRepository.deleteByMemberId(memberId);
        reportedGameRepository.deleteAllByGameId(gameIds);
        gameRepository.deleteAllByGameId(gameIds);

        List<Long> gameRoomHistoryIds = gameRoomHistoryRepositorySupport.findAllGameRoomHistoryIdByMemberId(
            memberId);

        log.info(gameRoomHistoryIds.toString());

        gameRoomHistoryRepository.deleteAllByGameId(gameRoomHistoryIds);

        memberRepository.deleteById(memberId);
    }

    /**
     * @param email
     * @return db에 입력된 email이 있는지
     */
    public boolean findUserBySocialId(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean isAdmin(Long reportId) {
        Member member = memberRepository.findById(reportId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return member.getRole() == Role.ADMIN;
    }

    @Transactional
    public void logout(String refreshToken) {
//        Long findUserId = tokenService.getUid(refreshToken);

        //Redis Cache에 저장
//        Long accessTokenTime = tokenService.getExpiration(accessToken);
        Long refreshTokenTime = tokenService.getExpiration(refreshToken);
//        if (accessTokenTime > 0) {
//            redisTemplate.opsForValue()
//                .set(accessToken, "logout", accessTokenTime,
//                    TimeUnit.MILLISECONDS);
//        }
//        if(refreshTokenTime > 0) {
//            redisTemplate.opsForValue()
//                .set(refreshToken, "logout", refreshTokenTime,
//                    TimeUnit.MILLISECONDS);
//        }
    }
    /*

     */
/**
 * @param code
 * @return Access 토큰
 *//*

    public String getKaKaoAccessToken(String code) {
        //카카오 서버에 POST 방식으로 엑세스 토큰을 요청
        //RestTemplate를 이용
//        RestTemplate rt = new RestTemplate();

        rt = new RestTemplate();

        //HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();

//        System.out.println("인가 코드 확인 :" + code);
//        System.out.println(env.getProperty("kakao.api_key"));
//        System.out.println(env.getProperty("kakao.login.redirect_uri"));

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", env.getProperty("kakao.api_key"));
        params.add("client_secret", env.getProperty("kakao.client-secret"));
        params.add("redirect_uri", env.getProperty("kakao.login.redirect_uri"));
        params.add("code", code);
        //HttpHeader와 HttpBody를 HttpEntity에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoRequest = new HttpEntity<>(params, headers);
        //카카오 서버에 HTTP 요청 - POST
        ResponseEntity<String> response = rt.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            kakaoRequest,
            String.class
        );

        System.out.println("response" + response);
        //응답에서 엑세스 토큰 받기
        JsonParser jp = new JsonParser();
        JsonObject jo = jp.parse(response.getBody()).getAsJsonObject();
        String accessToken = jo.get("access_token").getAsString();

        return accessToken;
    }

    */

    /**
     * @param accessToken
     * @return 카카오 사용자 정보
     *//*

    public ResponseEntity<String> getKakaoMember(String accessToken) {
        //엑세스 토큰을 통해 사용자 정보를 응답 받기

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> memberInfoRequest = new HttpEntity<>(headers);

        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> memberInfoResponse = rt.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.POST,
            memberInfoRequest,
            String.class
        );

        System.out.println("userinfo " + memberInfoResponse);

        return memberInfoResponse;
    }

    public String getGoogleAccessToken(String code) {
        RestTemplate rt = new RestTemplate();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(
                "https://www.googleapis.com/oauth2/v4/token")
            .queryParam("code", code)
            .queryParam("client_id", env.getProperty("login.google.client_id"))
            .queryParam("client_secret", env.getProperty("login.google.client_secret"))
            .queryParam("redirect_uri", env.getProperty("login.google.redirect_uri"))
            .queryParam("grant_type", "authorization_code");

        HttpEntity request = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = rt.exchange(
            uriBuilder.toUriString(),
            HttpMethod.POST,
            request,
            String.class
        );
        JsonParser jp = new JsonParser();
        JsonObject jo = jp.parse(response.getBody()).getAsJsonObject();

        return jo.get("access_token").getAsString();
    }

    public ResponseEntity<String> getGoogleMember(String accessToken) {
        rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        System.out.println(accessToken);
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity memberInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<String> responsememberInfo = rt.exchange(UriComponentsBuilder
                .fromHttpUrl("https://www.googleapis.com/oauth2/v2/userinfo")
                .toUriString(),
            HttpMethod.GET,
            memberInfoRequest,
            String.class);

        return responsememberInfo;
    }
*/
    @Transactional
    public Member banMember(Long memberId, BanLevel banLevel) {
        Member memberToModify = memberRepository.findById(memberId).get();

        memberToModify.setRole(Role.BAN);
        memberToModify.setReleaseDate(getReleaseDate(banLevel));

        return memberRepository.save(memberToModify);
    }

    @Transactional
    public Member releaseMember(Long memberId) {
        Member memberToModify = memberRepository.findById(memberId).get();

        memberToModify.setRole(Role.MEMBER);
        memberToModify.setReleaseDate(null);

        return memberRepository.save(memberToModify);
    }

    public LocalDate getReleaseDate(BanLevel banLevel) {
        LocalDate today = LocalDate.now();

        switch (banLevel) {
            case oneWeek:
                today.plusDays(7);
                break;
            case oneMonth:
                today.plusMonths(1);
                break;
            case sixMonth:
                today.plusMonths(6);
                break;
            case oneYear:
                today.plusYears(1);
                break;
            case endless:
                today.plusYears(9999);
                break;
        }

        return today;
    }
}
