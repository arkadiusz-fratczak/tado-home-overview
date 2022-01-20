package tado.homeoverview


import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class HomeOverviewApplicationSpec extends Specification {

    def "should load spring context"() {
        expect: "spring context is successfully created"
        true
    }
}
