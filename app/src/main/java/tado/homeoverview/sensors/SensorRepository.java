package tado.homeoverview.sensors;

import org.springframework.data.jpa.repository.JpaRepository;
import tado.homeoverview.sensors.domain.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
}
