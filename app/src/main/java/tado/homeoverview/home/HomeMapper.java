package tado.homeoverview.home;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tado.homeoverview.api.model.CreateHomeDTO;
import tado.homeoverview.api.model.CreateRoomDTO;
import tado.homeoverview.api.model.DetailedHomeDTO;
import tado.homeoverview.api.model.DetailedRoomDTO;
import tado.homeoverview.api.model.HomeDTO;
import tado.homeoverview.api.model.SensorDTO;
import tado.homeoverview.home.domain.Home;
import tado.homeoverview.home.domain.Room;
import tado.homeoverview.sensors.domain.Sensor;

@Mapper(componentModel = "spring")
public interface HomeMapper {

    @Mapping(source = "id", target = "homeId")
    HomeDTO toHomeDTO(Home home);

    @Mapping(source = "id", target = "homeId")
    DetailedHomeDTO toDetailedHomeDTO(Home home);

    @Mapping(source = "id", target = "roomId")
    DetailedRoomDTO toDetailedRoomDTO(Room room);

    @Mapping(source = "id", target = "sensorId")
    SensorDTO toSensorDTO(Sensor sensor);

    Home toHomeEntity(CreateHomeDTO createHomeDTO);

    Room toRoomEntity(CreateRoomDTO createRoomDTO);
}
