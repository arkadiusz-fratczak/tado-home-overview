package tado.homeoverview.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tado.homeoverview.home.domain.Home;
import tado.homeoverview.home.domain.Room;
import tado.homeoverview.sensors.SensorRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class HomeService {

    private final HomeRepository homeRepository;
    private final RoomRepository roomRepository;
    private final SensorRepository sensorRepository;

    public List<Home> getAllHomes() {
        return homeRepository.findAll();
    }

    public Optional<Home> getHomeById(Long homeId) {
        return homeRepository.findById(homeId);
    }

    public List<Home> getHomesByResident(String residentId) {
        return homeRepository.findAllByResident(residentId);
    }

    public Home createHome(Home home) {
        home.getResidents().add(home.getOwner());
        return homeRepository.save(home);
    }

    public Optional<Home> addResident(Long homeId, String userId) {
        return homeRepository.findById(homeId)
                .map(h -> homeRepository.save(h.addResident(userId)));
    }

    public Optional<Room> createRoom(Long homeId, Room room) {
        return homeRepository.findById(homeId).map(h -> {
            room.setHome(h);
            return roomRepository.save(room);
        });
    }

    public Optional<Room> getRoomById(Long homeId, Long roomId) {
        return roomRepository.findRoomAssociatedWithHomeById(homeId, roomId);
    }

    public Optional<Room> addSensorToRoom(Long homeId, Long roomId, Long sensorId) {
        var roomOpt = roomRepository.findRoomAssociatedWithHomeById(homeId, roomId);
        if (roomOpt.isPresent()) {
            var sensorOpt = sensorRepository.findById(sensorId);
            return sensorOpt.map(s -> roomRepository.save(roomOpt.get().addSensor(s)));
        }
        return Optional.empty();
    }

    public Optional<Room> updateRoomExpectedTemperature(Long homeId, Long roomId, Float expectedTemperature) {
        return roomRepository.findRoomAssociatedWithHomeById(homeId, roomId)
                .map(r -> r.withExpectedTemperature(expectedTemperature))
                .map(roomRepository::save);
    }
}
