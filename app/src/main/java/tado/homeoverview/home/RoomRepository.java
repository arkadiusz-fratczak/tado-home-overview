package tado.homeoverview.home;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tado.homeoverview.home.domain.Room;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.id = :roomId AND r.home.id = :homeId")
    Optional<Room> findRoomAssociatedWithHomeById(@Param("homeId") Long homeId, @Param("roomId") Long roomId);
}
