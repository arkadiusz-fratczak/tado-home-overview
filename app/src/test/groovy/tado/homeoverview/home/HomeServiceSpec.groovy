package tado.homeoverview.home

import spock.lang.Specification
import tado.homeoverview.home.domain.Home
import tado.homeoverview.home.domain.Room
import tado.homeoverview.sensors.SensorRepository
import tado.homeoverview.sensors.domain.Sensor

class HomeServiceSpec extends Specification {

    HomeRepository homeRepository = Mock()
    RoomRepository roomRepository = Mock()
    SensorRepository sensorRepository = Mock()

    HomeService homeService = new HomeService(homeRepository, roomRepository, sensorRepository)

    def "should add owner to home's residents during saving of home"() {
        given:
            def homeToCreate = new Home(null, "Joe", "Joe's home", new HashSet<>(), new HashSet<>())
            homeRepository.save(_ as Home) >> { Home home -> home.withId(1L) }
        when:
            def result = homeService.createHome(homeToCreate)
        then:
            result.getResidents().contains("Joe")
    }

    def "should create room only if home exists"() {
        given:
            def roomToCreate = new Room(null, "room", 22.4, Set.of(), null)
            roomRepository.save(_ as Room) >> { Room room -> room.withId(12L) }
            homeRepository.findById(1L) >> Optional.of(new Home(1L, "Joe", "Joe's home", new HashSet<>(), new HashSet<Room>()))
            homeRepository.findById(2L) >> Optional.empty()
        expect:
            homeService.createRoom(1L, roomToCreate).isPresent()
            homeService.createRoom(2L, roomToCreate).isEmpty()
    }

    def "should attach sensor to room only if all entities exist"() {
        given:
            roomRepository.save(_ as Room) >> { Room room -> room.withId(12L) }
            roomRepository.findRoomAssociatedWithHomeById(1L, 12L) >> Optional.of(new Room(1L, "Bedroom", 22.4, new HashSet<>(), new Home(1L)))
            roomRepository.findRoomAssociatedWithHomeById(1L, 13L) >> Optional.empty()
            sensorRepository.findById(123L) >> Optional.of(new Sensor(123L))
            sensorRepository.findById(321L) >> Optional.empty()
        expect:
            homeService.addSensorToRoom(1L, 12L, 123L).isPresent() // home, room and sensor exist
            homeService.addSensorToRoom(1L, 12L, 321L).isEmpty() // home, room exist but sensor not
            homeService.addSensorToRoom(1L, 13L, 123L).isEmpty() // sensor exists, but home or room not
    }
}
