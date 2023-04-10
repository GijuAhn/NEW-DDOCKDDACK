package com.ddockddack.domain.bestcut.service;

import com.ddockddack.domain.bestcut.entity.Bestcut;
import com.ddockddack.domain.bestcut.entity.BestcutLike;
import com.ddockddack.domain.bestcut.repository.BestcutLikeRepository;
import com.ddockddack.domain.bestcut.repository.BestcutRepository;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AlreadyExistResourceException;
import com.ddockddack.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BestcutLikeService {

    private final BestcutLikeRepository bestcutLikeRepository;
    private final BestcutRepository bestcutRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveBestcutLike(Long bestcutId, Long memberId) {
        Bestcut bestcut = bestcutRepository.findById(bestcutId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BESTCUT_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (bestcutLikeRepository.findOne(bestcutId, memberId).isPresent()) {
            throw new AlreadyExistResourceException(ErrorCode.ALREADY_EXIST_BESTCUT_LIKE);
        }

        BestcutLike bestcutLike = BestcutLike.builder()
                .bestcut(bestcut)
                .member(member)
                .build();

        bestcutLikeRepository.save(bestcutLike);
    }

    @Transactional
    public void removeBestcutLike(Long bestcutId, Long memberId) {
        BestcutLike bestcutLike = bestcutLikeRepository.findOne(bestcutId, memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BESTCUT_LIKE_NOT_FOUND));
        bestcutLikeRepository.delete(bestcutLike);
    }
}
