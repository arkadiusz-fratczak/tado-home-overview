package tado.homeoverview.sensors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tado.homeoverview.api.model.CreateSensorDTO;
import tado.homeoverview.api.model.SensorDTO;
import tado.homeoverview.sensors.domain.Sensor;

@Mapper(componentModel = "spring")
public interface SensorMapper {

    Sensor toSensorEntity(CreateSensorDTO createSensorDTO);

    @Mapping(source = "id", target = "sensorId")
    SensorDTO toSensorDTO(Sensor sensor);
}
