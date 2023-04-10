package com.ddockddack.domain.member.repository;

import com.ddockddack.domain.member.entity.Member;
import org.springframework.core.env.PropertyResolverExtensionsKt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Member getByEmail(String email);


//    public Member getBySocialId(String email) {return }
}
