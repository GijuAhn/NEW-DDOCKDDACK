package com.ddockddack.domain.member.repository;

import com.ddockddack.domain.member.entity.Member;
import io.micrometer.core.annotation.Counted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Member getByEmail(String email);

    @Counted("ddockddack.signup")
    Member save(Member member);
//    public Member getBySocialId(String email) {return }
}
