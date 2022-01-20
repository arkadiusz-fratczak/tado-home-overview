package tado.homeoverview.sensors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tado.homeoverview.api.SensorsApi;
import tado.homeoverview.api.model.CreateSensorDTO;
import tado.homeoverview.api.model.SensorDTO;
import tado.homeoverview.api.model.UpdateSensorStateDTO;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Transactional
public class SensorsController implements SensorsApi {

    private final SensorsRepository sensorsRepository;
    private final SensorsMapper sensorsMapper;

    @Override
    public ResponseEntity<SensorDTO> createSensor(CreateSensorDTO createSensorDTO) {
        var sensor = sensorsRepository.save(sensorsMapper.toSensorEntity(createSensorDTO));
        return new ResponseEntity<>(sensorsMapper.toSensorDTO(sensor), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<SensorDTO>> getAllSensors() {
        var sensors = StreamSupport.stream(sensorsRepository.findAll().spliterator(), false)
                .map(sensorsMapper::toSensorDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sensors);
    }

    @Override
    public ResponseEntity<SensorDTO> getSensorById(Long sensorId) {
        var sensorOpt = sensorsRepository.findById(sensorId);
        return ResponseEntity.of(sensorOpt.map(sensorsMapper::toSensorDTO));
    }

    @Override
    public ResponseEntity<Void> updateSensorState(Long sensorId, UpdateSensorStateDTO updateSensorStateDTO) {
        var sensorOpt = sensorsRepository.findById(sensorId);
        return sensorOpt
                .map(s -> sensorsRepository.save(s.withValue(updateSensorStateDTO.getValue())))
                .map(s -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
