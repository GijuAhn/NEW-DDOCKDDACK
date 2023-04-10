package com.ddockddack.domain.member.service;

import com.ddockddack.domain.bestcut.service.BestcutService;
import com.ddockddack.domain.game.repository.GameImageRepository;
import com.ddockddack.domain.game.repository.GameRepository;
import com.ddockddack.domain.game.repository.StarredGameRepository;
import com.ddockddack.domain.gameRoom.repository.GameRoomHistoryRepository;
import com.ddockddack.domain.gameRoom.repository.GameRoomHistoryRepositorySupport;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.entity.Role;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.member.request.MemberModifyNameReq;
import com.ddockddack.domain.member.response.MemberInfoRes;
import com.ddockddack.domain.report.repository.ReportedGameRepository;
import com.ddockddack.global.aws.AwsS3;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.ImageExtensionException;
import com.ddockddack.global.error.exception.NotFoundException;
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
    private final GameRepository gameRepository;
    private final GameRoomHistoryRepository gameRoomHistoryRepository;
    private final AwsS3 awsS3;


    //    private final RedisTemplate redisTemplate;
    private RestTemplate rt;

    @Transactional
    public void modifyMemberNickname(Long memberId, MemberModifyNameReq modifyMemberNickname) {
        Member member = memberRepository.findById(memberId).get();
        log.info("log! {}, {}", modifyMemberNickname.getNickname(),
            modifyMemberNickname.getNickname().isEmpty());
        if (!member.getNickname().equals(modifyMemberNickname.getNickname())) {
            member.modifyNickname(modifyMemberNickname.getNickname());
        }
    }

    @Transactional
    public String modifyMemberProfile(Long memberId, MultipartFile modifyProfileImg) {
        Member member = memberRepository.findById(memberId).get();

        if (modifyProfileImg.isEmpty()) {
            throw new NotFoundException(ErrorCode.MISSING_REQUIRED_VALUE);
        }

        if (!(modifyProfileImg.getContentType().contains("image/jpg") ||
            (modifyProfileImg.getContentType().contains("image/jpeg") ||
                (modifyProfileImg.getContentType().contains("image/png"))))) {
            throw new ImageExtensionException(ErrorCode.EXTENSION_NOT_ALLOWED);
        }

        log.info("modifyProfileImg contentType {}", modifyProfileImg.getContentType());

        try {
            String fileName = awsS3.multipartFileUpload(modifyProfileImg);
            if (!member.getProfile().equals("default_profile_img.png")) {
                awsS3.deleteObject(member.getProfile());
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
    public MemberInfoRes memberDetails(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberInfoRes.of(member.getId(), member.getEmail(), member.getNickname(),
            member.getProfile(), member.getRole());
    }

    @Transactional
    public void removeMemberById(Long memberId) {
        List<Long> bestcutIds = bestcutService.findBestcutByMemberId(memberId);
        bestcutService.removeBestcutByMemberId(memberId);
        bestcutService.removeAllBestcutByIds(bestcutIds);

        List<Long> gameIds = gameRepository.findGameIdsByMemberId(memberId);
        gameImageRepository.deleteAllByGameId(gameIds);
        starredGameRepository.deleteByMemberId(memberId);
        starredGameRepository.deleteAllByGameId(gameIds);
        reportedGameRepository.deleteByMemberId(memberId);
        reportedGameRepository.deleteAllByGameId(gameIds);
        gameRepository.deleteAllByGameId(gameIds);

        List<Long> gameRoomHistoryIds = gameRoomHistoryRepository.findAllGameRoomHistoryIdByMemberId(
            memberId);

        log.info(gameRoomHistoryIds.toString());

        gameRoomHistoryRepository.deleteAllByGameId(gameRoomHistoryIds);

        memberRepository.deleteById(memberId);
    }

    /**
     * @param email
     * @return db에 입력된 email이 있는지
     */
    public boolean findMemberByEmail(String email) {
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

    @Transactional
    public Member banMember(Long memberId, BanLevel banLevel) {
        Member memberToModify = memberRepository.findById(memberId).get();

        memberToModify.setRole(Role.BAN);
        memberToModify.setReleaseDate(findReleaseDate(banLevel));

        return memberRepository.save(memberToModify);
    }

    @Transactional
    public Member releaseMember(Long memberId) {
        Member memberToModify = memberRepository.findById(memberId).get();

        memberToModify.setRole(Role.MEMBER);
        memberToModify.setReleaseDate(null);

        return memberRepository.save(memberToModify);
    }

    public LocalDate findReleaseDate(BanLevel banLevel) {
        LocalDate today = LocalDate.now();

        switch (banLevel) {
            case ONE_WEEK:
                today.plusDays(7);
                break;
            case ONE_MONTH:
                today.plusMonths(1);
                break;
            case SIX_MONTH:
                today.plusMonths(6);
                break;
            case ONE_YEAR:
                today.plusYears(1);
                break;
            case ENDLESS:
                today.plusYears(9999);
                break;
        }

        return today;
    }
}
