package com.ddockddack.domain.admin.service;

import com.ddockddack.domain.member.service.MemberService;
import com.ddockddack.domain.member.service.TokenService;
import com.ddockddack.domain.report.repository.ReportedBestcutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final MemberService memberService;
    public boolean isAdminByAccessToken(Long adminId){
        return memberService.isAdmin(adminId);
    }
}
