package tado.homeoverview.home

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
import tado.homeoverview.api.model.CreateHomeDTO
import tado.homeoverview.api.model.CreateRoomDTO
import tado.homeoverview.api.model.DetailedHomeDTO
import tado.homeoverview.api.model.DetailedRoomDTO
import tado.homeoverview.home.domain.Home
import tado.homeoverview.home.domain.Room
import tado.homeoverview.sensors.domain.Sensor

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

@AutoConfigureJsonTesters
@WebMvcTest(HomeController)
@Import(HomeMapperImpl)
class HomeControllerSpec extends Specification {

    @Shared
    String HOME_ENDPOINT = "/api/homes"

    @Autowired
    MockMvc mvc;

    @Autowired
    HomeMapper homeMapper;

    @SpringBean
    HomeService homeService = Mock();

    @Autowired
    JacksonTester<CreateHomeDTO> jsonRequestCreateHome;

    @Autowired
    JacksonTester<CreateRoomDTO> jsonRequestCreateRoom;

    @Autowired
    JacksonTester<DetailedHomeDTO> jsonResponseHome;

    @Autowired
    JacksonTester<DetailedRoomDTO> jsonResponseRoom;

    def 'should create home'() {
        given:
            def createHomeDTO = createHomeDTO("Joe", "Joe's home")
            def expectedHome = new Home(1L, "Joe", "Joe's home", Set.of(), Set.of())
        when:
            def response = mvc.perform(
                    post(HOME_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestCreateHome.write(createHomeDTO).getJson()))
                    .andReturn().getResponse()
        then:
            1 * homeService.createHome({ it.owner == "Joe" && it.name == "Joe's home" }) >> expectedHome
            response.getStatus() == HttpStatus.CREATED.value()
            response.getContentAsString() == jsonResponseHome.write(homeMapper.toDetailedHomeDTO(expectedHome)).getJson()
    }

    def 'should add resident to home'() {
        given:
            def homeId = 1L
            def resident = "Merry"
        when:
            def response = mvc.perform(
                    put("${HOME_ENDPOINT}/${homeId}/residents/${resident}"))
                    .andReturn().getResponse()
        then:
            1 * homeService.addResident(homeId, resident) >> serviceResponse
            response.getStatus() == status
        where:
            serviceResponse                                                     | status
            Optional.of(new Home(1L, "Joe", "name", Set.of("Merry"), Set.of())) | HttpStatus.NO_CONTENT.value()
            Optional.empty()                                                    | HttpStatus.NOT_FOUND.value()
    }

    def 'should create room'() {
        given:
            def createRoomDTO = createRoomDTO("Bedroom")
        when:
            def response = mvc.perform(
                    post("${HOME_ENDPOINT}/1/rooms")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestCreateRoom.write(createRoomDTO).getJson()))
                    .andReturn().getResponse()
        then:
            1 * homeService.createRoom(1L, { it.name == "Bedroom" }) >> serviceResponse
            response.getStatus() == status
        where:
            serviceResponse                                                    | status
            Optional.of(new Room(1L, "Bedroom", 22.4, Set.of(), new Home(1L))) | HttpStatus.CREATED.value()
            Optional.empty()                                                   | HttpStatus.NOT_FOUND.value()
    }

    def 'should assign sensor to room'() {
        given:
            def homeId = 1L
            def roomId = 11L
            def sensorId = 111L
        when:
            def response = mvc.perform(
                    put("${HOME_ENDPOINT}/${homeId}/rooms/${roomId}/sensors/${sensorId}"))
                    .andReturn().getResponse()
        then:
            1 * homeService.addSensorToRoom(homeId, roomId, sensorId) >> serviceResponse
            response.getStatus() == status
        where:
            serviceResponse                                                            | status
            Optional.of(new Room(1L, "Bedroom", 22.4, Set.of(new Sensor(1L)), new Home(1L))) | HttpStatus.NO_CONTENT.value()
            Optional.empty()                                                           | HttpStatus.NOT_FOUND.value()
    }

    def 'should get home details by home ID'() {
        given:
            def existingSensor = new Sensor(123L, "TEMP", "CELSIUS", 21.4)
            def existingRoom = new Room(12L, "Bedroom", 22.4, Set.of(existingSensor), new Home(1L))
            def existingHome = new Home(1L, "Joe", "Joe's home", Set.of("Joe"), Set.of(existingRoom))
            homeService.getHomeById(1L) >> Optional.of(existingHome)
        when:
            def response = mvc.perform(get("${HOME_ENDPOINT}/${existingHome.id}"))
                    .andReturn().getResponse()
        then:
            response.getStatus() == HttpStatus.OK.value()
            response.getContentAsString() == jsonResponseHome.write(homeMapper.toDetailedHomeDTO(existingHome)).getJson()
    }

    def 'should return 404 HTTP status when home with given ID not exists'() {
        homeService.getHomeById(1L) >> Optional.empty()
        when:
            def response = mvc.perform(get("${HOME_ENDPOINT}/1"))
                    .andReturn().getResponse()
        then:
            response.getStatus() == HttpStatus.NOT_FOUND.value()
    }

    def 'should get room details'() {
        given:
            def existingSensor = new Sensor(123L, "TEMP", "CELSIUS", 21.4)
            def existingRoom = new Room(12L, "Bedroom", 22.4, Set.of(existingSensor), new Home(1L))
            homeService.getRoomById(1L, 12L) >> Optional.of(existingRoom)
        when:
            def response = mvc.perform(get("${HOME_ENDPOINT}/1/rooms/${existingRoom.id}"))
                    .andReturn().getResponse()
        then:
            response.getStatus() == HttpStatus.OK.value()
            response.getContentAsString() == jsonResponseRoom.write(homeMapper.toDetailedRoomDTO(existingRoom)).getJson()
    }

    def 'should return 404 HTTP status when room or home with given ID not exists'() {
        homeService.getRoomById(1L, 11L) >> Optional.empty()
        when:
            def response = mvc.perform(get("${HOME_ENDPOINT}/1/rooms/11"))
                    .andReturn().getResponse()
        then:
            response.getStatus() == HttpStatus.NOT_FOUND.value()
    }

    CreateHomeDTO createHomeDTO(String owner, String homeName) {
        def home = new CreateHomeDTO()
        home.setOwner(owner)
        home.setName(homeName)
        home
    }

    CreateRoomDTO createRoomDTO(String name) {
        def room = new CreateRoomDTO()
        room.setName(name)
        room
    }
}
