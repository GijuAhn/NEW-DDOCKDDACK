package com.ddockddack.domain.bestcut.service;

 import com.ddockddack.domain.bestcut.entity.Bestcut;
 import com.ddockddack.domain.bestcut.repository.BestcutLikeRepository;
 import com.ddockddack.domain.bestcut.repository.BestcutRepository;
import com.ddockddack.domain.bestcut.repository.BestcutRepositorySupport;
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
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AccessDeniedException;
import com.ddockddack.global.error.exception.AlreadyExistResourceException;
import com.ddockddack.global.error.exception.NotFoundException;
import com.ddockddack.global.service.AwsS3Service;
import com.ddockddack.global.util.PageCondition;
import com.ddockddack.global.util.PageConditionReq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BestcutService {

    private final BestcutRepository bestcutRepository;
    private final BestcutLikeRepository bestcutLikeRepository;
    private final ReportedBestcutRepository reportedBestcutRepository;
    private final MemberRepository memberRepository;
    private final GameRoomRepository gameRoomRepository;
    private final AwsS3Service awsS3Service;
    private final BestcutRepositorySupport bestcutRepositorySupport;


    /**
     * 베스트컷 이미지 게시
     *
     * @param memberId
     * @param saveReq
     */
    @Transactional
    public void saveBestcut(Long memberId, BestcutSaveReq saveReq) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        String pinNumber = saveReq.getPinNumber();
        String socketId = saveReq.getSocketId();

        for (BestcutImageReq imageReq : saveReq.getImages()) {
            int index = imageReq.getBestcutIndex();

            byte[] byteImage = gameRoomRepository.findByImageIndex(pinNumber, socketId, index);
            String fileName = awsS3Service.InputStreamUpload(byteImage);

            Bestcut bestcut = Bestcut.builder()
                    .member(member)
                    .gameTitle(saveReq.getGameTitle())
                    .gameImageUrl(imageReq.getGameImgUrl())
                    .gameImgDesc(imageReq.getGameImgDesc())
                    .imageUrl(fileName)
                    .title(imageReq.getBestcutImgTitle())
                    .build();

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
        Bestcut bestcut = bestcutRepository.findById(bestcutId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BESTCUT_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getRole() != Role.ADMIN && !bestcut.getMember().getId().equals(memberId)) {
            throw new AccessDeniedException(ErrorCode.NOT_AUTHORIZED);
        }

        bestcutLikeRepository.deleteByBestcutId(bestcutId);
        reportedBestcutRepository.deleteByBestcutId(bestcutId);

        awsS3Service.deleteObject(bestcut.getImageUrl());
        bestcutRepository.delete(bestcut);
    }

    @Transactional
    public void removeAllByIds(List<Long> bestcutIds) {
        bestcutLikeRepository.deleteByBestcutIdIn(bestcutIds);
        reportedBestcutRepository.deleteByBestcutIdIn(bestcutIds);

        bestcutRepository.deleteAllByIds(bestcutIds);
    }

    @Transactional
    public void removeByMemberId(Long memberId){
        bestcutLikeRepository.deleteByMemberId(memberId);
        reportedBestcutRepository.deleteByMemberId(memberId);
    }

    @Transactional
    public void reportBestcut(Long memberId, Long bestcutId, ReportType reportType) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new org.webjars.NotFoundException("존재하지 않는 멤버"));
        Bestcut bestcut = bestcutRepository.findById(bestcutId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BESTCUT_NOT_FOUND));
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
    public PageImpl<BestcutRes> findAll(Boolean my, Long loginMemberId,
                                        PageConditionReq pageConditionReq) {
        PageCondition pageCondition = pageConditionReq.toEntity();
        return bestcutRepository.findAllBySearch(my, loginMemberId, pageCondition);
    }

    public List<Long> findByMemberId(Long memberId){
        return bestcutRepository.findByAllMemberId(memberId);
    }

    public BestcutRes findOne(Long loginMemberId, Long bestcutId) {
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

        return bestcutRepositorySupport.findAllReportedBestcut();
    }

}
