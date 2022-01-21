package tado.homeoverview.sensors.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
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

    @With
    private Float value;
}
