package tado.homeoverview.sensors

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Shared
import spock.lang.Specification
import tado.homeoverview.api.model.CreateSensorDTO
import tado.homeoverview.api.model.SensorDTO
import tado.homeoverview.api.model.UpdateSensorStateDTO
import tado.homeoverview.sensors.domain.Sensor

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

@AutoConfigureJsonTesters
@WebMvcTest(SensorController)
@Import(SensorMapperImpl)
class SensorControllerSpec extends Specification {

    @Shared
    String SENSORS_ENDPOINT = "/api/sensors"

    @Autowired
    MockMvc mvc;

    @Autowired
    SensorMapper sensorMapper

    @SpringBean
    SensorRepository sensorRepository = Mock()

    @Autowired
    JacksonTester<CreateSensorDTO> jsonRequestSensor;

    @Autowired
    JacksonTester<UpdateSensorStateDTO> jsonRequestUpdateSensor;

    @Autowired
    JacksonTester<SensorDTO> jsonResponseSensor;

    def "should return sensor matching given ID"() {
        given:
            def expectedSensor = new Sensor(1L, "TEMP", "CELSIUS", 21.4)
            sensorRepository.findById(expectedSensor.getId()) >> Optional.of(expectedSensor)
        when:
            def response = mvc.perform(get("${SENSORS_ENDPOINT}/${expectedSensor.id}"))
                    .andReturn().getResponse()
        then:
            response.getStatus() == HttpStatus.OK.value()
            response.getContentAsString() == jsonResponseSensor.write(sensorMapper.toSensorDTO(expectedSensor)).getJson()
    }

    def "should return 404 HTTP status when sensor with given ID not exits"() {
        given:
            sensorRepository.findById(_ as Long) >> Optional.empty()
        when:
            def response = mvc.perform(get("${SENSORS_ENDPOINT}/1"))
                    .andReturn().getResponse()
        then:
            response.getStatus() == HttpStatus.NOT_FOUND.value()
    }

    def "should successfully create sensor"() {
        given:
            def createSensorDTO = createSensorDTO("TEMP", "CELSIUS")
            def expectedSensor = new Sensor(1L, "TEMP", "CELSIUS", null)
        when:
            def response = mvc.perform(
                    post(SENSORS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestSensor.write(createSensorDTO).getJson()))
                    .andReturn().getResponse()
        then:
            1 * sensorRepository.save({ it.valueType == "TEMP" && it.unit == "CELSIUS" }) >> expectedSensor
            response.getStatus() == HttpStatus.CREATED.value()
            response.getContentAsString() == jsonResponseSensor.write(sensorMapper.toSensorDTO(expectedSensor)).getJson()
    }

    def "should update sensor state"() {
        given:
            def existingSensor = new Sensor(1L, "TEMP", "CELSIUS", null)
            def updatedState = createUpdateSensorStateDTO(23.1)
        when:
            def response = mvc.perform(
                    put("${SENSORS_ENDPOINT}/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestUpdateSensor.write(updatedState).getJson()))
                    .andReturn().getResponse()
        then:
            1 * sensorRepository.findById(1L) >> Optional.of(existingSensor)
            1 * sensorRepository.save(existingSensor.withValue(23.1)) >> existingSensor.withValue(23.1)
            response.getStatus() == HttpStatus.NO_CONTENT.value()
    }

    private CreateSensorDTO createSensorDTO(String valueType, String unit) {
        def sensor = new CreateSensorDTO()
        sensor.setValueType(valueType)
        sensor.setUnit(unit)
        sensor
    }

    private UpdateSensorStateDTO createUpdateSensorStateDTO(Float value) {
        def sensorState = new UpdateSensorStateDTO()
        sensorState.setValue(value)
        sensorState
    }
}
