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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Transactional
public class SensorController implements SensorsApi {

    private final SensorRepository sensorRepository;
    private final SensorMapper sensorMapper;

    @Override
    public ResponseEntity<SensorDTO> createSensor(CreateSensorDTO createSensorDTO) {
        var sensor = sensorRepository.save(sensorMapper.toSensorEntity(createSensorDTO));
        return new ResponseEntity<>(sensorMapper.toSensorDTO(sensor), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<SensorDTO>> getAllSensors() {
        var sensors = sensorRepository.findAll().stream()
                .map(sensorMapper::toSensorDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sensors);
    }

    @Override
    public ResponseEntity<SensorDTO> getSensorById(Long sensorId) {
        var sensorOpt = sensorRepository.findById(sensorId);
        return ResponseEntity.of(sensorOpt.map(sensorMapper::toSensorDTO));
    }

    @Override
    public ResponseEntity<Void> updateSensorState(Long sensorId, UpdateSensorStateDTO updateSensorStateDTO) {
        var sensorOpt = sensorRepository.findById(sensorId);
        return sensorOpt
                .map(s -> sensorRepository.save(s.withValue(updateSensorStateDTO.getValue())))
                .map(s -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
