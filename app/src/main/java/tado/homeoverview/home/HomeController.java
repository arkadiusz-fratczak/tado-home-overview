package tado.homeoverview.home;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tado.homeoverview.api.HomesApi;
import tado.homeoverview.api.model.CreateHomeDTO;
import tado.homeoverview.api.model.CreateRoomDTO;
import tado.homeoverview.api.model.DetailedHomeDTO;
import tado.homeoverview.api.model.DetailedRoomDTO;
import tado.homeoverview.api.model.HomeDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class HomeController implements HomesApi {

    private final HomeService homeService;
    private final HomeMapper homeMapper;

    @Override
    public ResponseEntity<DetailedHomeDTO> getHomeById(Long homeId) {
        var homeOpt = homeService.getHomeById(homeId);
        return ResponseEntity.of(homeOpt.map(homeMapper::toDetailedHomeDTO));
    }

    @Override
    public ResponseEntity<List<HomeDTO>> getAllHomes(@Nullable String userAlias) {
        var homes = Optional.ofNullable(userAlias)
                .map(ua -> homeService.getHomesByResident(userAlias))
                .orElseGet(homeService::getAllHomes).stream()
                .map(homeMapper::toHomeDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(homes);
    }

    @Override
    public ResponseEntity<DetailedHomeDTO> createHome(CreateHomeDTO createHomeDTO) {
        var home = homeService.createHome(homeMapper.toHomeEntity(createHomeDTO));
        return new ResponseEntity<>(homeMapper.toDetailedHomeDTO(home), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> addResident(Long homeId, String userAlias) {
        var homeOpt = homeService.addResident(homeId, userAlias);
        return homeOpt.map(h -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<DetailedRoomDTO> createRoom(Long homeId, CreateRoomDTO createRoomDTO) {
        var roomOpt = homeService.createRoom(homeId, homeMapper.toRoomEntity(createRoomDTO));
        return roomOpt.map(r -> new ResponseEntity<>(homeMapper.toDetailedRoomDTO(r), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<DetailedRoomDTO> getRoomById(Long homeId, Long roomId) {
        var roomOpt = homeService.getRoomById(homeId, roomId);
        return ResponseEntity.of(roomOpt.map(homeMapper::toDetailedRoomDTO));
    }

    @Override
    public ResponseEntity<Void> addSensor(Long homeId, Long roomId, Long sensorId) {
        var roomOpt = homeService.addSensorToRoom(homeId, roomId, sensorId);
        return roomOpt.map(h -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
