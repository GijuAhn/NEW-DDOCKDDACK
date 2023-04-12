package com.ddockddack.domain.bestcut.service;

import com.ddockddack.domain.bestcut.entity.Bestcut;
import com.ddockddack.domain.bestcut.entity.BestcutLike;
import com.ddockddack.domain.bestcut.repository.BestcutLikeRepository;
import com.ddockddack.domain.bestcut.repository.BestcutRepository;
import com.ddockddack.domain.bestcut.request.BestcutImageReq;
import com.ddockddack.domain.bestcut.request.BestcutSaveReq;
import com.ddockddack.domain.bestcut.response.BestcutRes;
import com.ddockddack.domain.bestcut.response.ReportedBestcutRes;
import com.ddockddack.domain.gameRoom.repository.GameRoomRepository;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.entity.Role;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.report.entity.ReportType;
import com.ddockddack.domain.report.entity.ReportedBestcut;
import com.ddockddack.domain.report.repository.ReportedBestcutRepository;
import com.ddockddack.global.aws.AwsS3;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AccessDeniedException;
import com.ddockddack.global.error.exception.AlreadyExistResourceException;
import com.ddockddack.global.error.exception.NotFoundException;
import com.ddockddack.global.util.PageCondition;
import com.ddockddack.global.util.PageConditionReq;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BestcutService {

    private final BestcutRepository bestcutRepository;
    private final BestcutLikeRepository bestcutLikeRepository;
    private final ReportedBestcutRepository reportedBestcutRepository;
    private final MemberRepository memberRepository;
    private final GameRoomRepository gameRoomRepository;
    private final AwsS3 awsS3;


    /**
     * 베스트컷 이미지 게시
     *
     * @param memberId
     * @param saveReq
     */
    @Transactional
    public void saveBestcut(Long memberId, BestcutSaveReq saveReq) {
        Member member = validAndGetMember(memberId);

        //게임방의 핀 번호
        String pinNumber = saveReq.getPinNumber();
        //유저 socket ID
        String socketId = saveReq.getSocketId();

        //유저가 업로드하고자 하는 모든 베스트컷을 gameRoom 객체에서 찾아와 s3와 DB에 저장
        for (int idx = 0; idx < saveReq.getImages().size(); idx++) {
            int userImageIndex = saveReq.getImages().get(idx).getBestcutIndex();
            byte[] byteImage = gameRoomRepository.findByImageIndex(pinNumber, socketId, userImageIndex);
            String fileName = awsS3.InputStreamUpload(byteImage);

            Bestcut bestcut = saveReq.toEntity(member, idx, fileName);
            bestcutRepository.save(bestcut);
        }
    }

    /**
     * 삭제하려는 member의 id와 베스트컷이 참조하는 member의 id가 다르면 예외 발생
     *
     * @param bestcutId
     * @param memberId
     */
    @Transactional
    public void removeBestcut(Long bestcutId, Long memberId) {
        Bestcut bestcut = validAndGetBestcut(bestcutId);
        Member member = validAndGetMember(memberId);

        //관리자가 아니거나 멤버 아이디가 베스트컷의 주인과 다른 경우 예외 발생
        if (member.getRole() != Role.ADMIN && !bestcut.getMember().getId().equals(memberId)) {
            throw new AccessDeniedException(ErrorCode.NOT_AUTHORIZED);
        }

        //해당 베스트컷의 좋아요 삭제 및 신고 기록 삭제
        bestcutLikeRepository.deleteByBestcutId(bestcutId);
        reportedBestcutRepository.deleteByBestcutId(bestcutId);

        //s3에서 삭제
        awsS3.deleteObject(bestcut.getImageUrl());
        bestcutRepository.delete(bestcut);
    }

    /**
     * List에 존재하는 모든 베스트컷 삭제
     * 해당 멤버가 업로드한 베스트컷의 좋아요, 신고 기록 삭제
     * 회원 탈퇴 시 사용
     * @param bestcutIds
     */
    @Transactional
    public void removeAllBestcutByIds(List<Long> bestcutIds) {
        bestcutLikeRepository.deleteByBestcutIdIn(bestcutIds);
        reportedBestcutRepository.deleteByBestcutIdIn(bestcutIds);

        bestcutRepository.deleteAllById(bestcutIds);
    }

    /**
     * 해당 멤버가 좋아요, 신고한 기록 삭제
     * 회원 탈퇴 시 사용
     * @param memberId
     */
    @Transactional
    public void removeBestcutByMemberId(Long memberId) {
        bestcutLikeRepository.deleteByMemberId(memberId);
        reportedBestcutRepository.deleteByMemberId(memberId);
    }

    /**
     * 베스트컷 신고
     *
     * @param memberId
     * @param bestcutId
     * @param reportType
     */
    @Transactional
    public void reportBestcut(Long memberId, Long bestcutId, ReportType reportType) {
        Member member = validAndGetMember(memberId);
        Bestcut bestcut = validAndGetBestcut(bestcutId);

        //이미 신고 기록있으면 예외 발생
        if (reportedBestcutRepository.existsByReportMemberIdAndBestcutId(memberId, bestcutId)) {
            throw new AlreadyExistResourceException(ErrorCode.ALREADY_EXIST_REPORT);
        }

        ReportedBestcut reportedBestcut = ReportedBestcut.builder()
            .reportMember(member)
            .reportedMember(bestcut.getMember())
            .bestcut(bestcut)
            .reportType(reportType)
            .build();

        reportedBestcutRepository.save(reportedBestcut);
    }

    /**
     * @param my               내 베스트컷 조회시 true
     * @param loginMemberId
     * @param pageConditionReq
     * @return
     */
    public PageImpl<BestcutRes> findAllBestcuts(Boolean my, Long loginMemberId,
        PageConditionReq pageConditionReq) {
        PageCondition pageCondition = pageConditionReq.toEntity();
        return bestcutRepository.findAllBySearch(my, loginMemberId, pageCondition);
    }

    /**
     * memberId로 베스트컷 검색
     *
     * @param memberId
     * @return
     */
    public List<Long> findBestcutByMemberId(Long memberId) {
        return bestcutRepository.findAllBestcutIdByMemberId(memberId);
    }

    /**
     * 베스트컷 상세 조회
     *
     * @param loginMemberId
     * @param bestcutId
     * @return
     */
    public BestcutRes findBestcut(Long loginMemberId, Long bestcutId) {
        return bestcutRepository.findOne(loginMemberId, bestcutId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.BESTCUT_NOT_FOUND));
    }

    /**
     * 신고 된 게임 목록 전체 조회하기
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<ReportedBestcutRes> findAllReportedBestCuts() {

        return bestcutRepository.findAllReportedBestcut();
    }

    /**
     * 베스트컷 좋아요
     *
     * @param bestcutId
     * @param memberId
     */
    @Transactional
    public void saveBestcutLike(Long bestcutId, Long memberId) {
        Bestcut bestcut = validAndGetBestcut(bestcutId);
        Member member = validAndGetMember(memberId);
        if (bestcutLikeRepository.existsByMemberIdAndBestcutId(memberId, bestcutId)) {
            throw new AlreadyExistResourceException(ErrorCode.ALREADY_EXIST_BESTCUT_LIKE);
        }

        BestcutLike bestcutLike = BestcutLike.builder()
            .bestcut(bestcut)
            .member(member)
            .build();

        bestcutLikeRepository.save(bestcutLike);
    }

    /**
     * 베스트컷 좋아요 취소
     *
     * @param bestcutId
     * @param memberId
     */
    @Transactional
    public void removeBestcutLike(Long bestcutId, Long memberId) {

        BestcutLike bestcutLike = bestcutLikeRepository.findByMemberIdAndBestcutId(memberId, bestcutId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.BESTCUT_LIKE_NOT_FOUND));
        bestcutLikeRepository.delete(bestcutLike);
    }


    private Bestcut validAndGetBestcut(Long bestcutId) {
        return bestcutRepository.findById(bestcutId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BESTCUT_NOT_FOUND));
    }

    private Member validAndGetMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

}
