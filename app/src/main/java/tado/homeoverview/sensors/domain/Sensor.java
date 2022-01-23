package tado.homeoverview.sensors.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {

    public Sensor(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String valueType;

    private String unit;

    private Float value;
}
