package com.ddockddack.domain.gameRoom.repository;

import com.ddockddack.domain.gameRoom.entity.GameRoom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRoomRedisRepository extends CrudRepository<GameRoom, String> {
}
