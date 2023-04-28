package com.ddockddack.domain.gameroom.repository;

import com.ddockddack.domain.gameroom.entity.GameMember;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameMemberRedisRepository extends CrudRepository<GameMember, String> {
    List<GameMember> findByPinNumber(String pinNumber);
}
