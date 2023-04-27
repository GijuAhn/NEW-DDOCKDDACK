package com.ddockddack.domain.gameroom.repository;

import com.ddockddack.domain.gameroom.entity.GameRoom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRoomRedisRepository extends CrudRepository<GameRoom, String> {
}
