package tado.homeoverview.home.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Home {

    @Id
    @GeneratedValue
    private Long id;

    private String owner;

    private String name;

    @ElementCollection
    private Set<String> residents = new HashSet<>();

    @OneToMany(orphanRemoval = true, mappedBy = "home")
    private Set<Room> rooms = new HashSet<>();

    public Home addRoom(Room room) {
        this.getRooms().add(room);
        room.setHome(this);
        return this;
    }

    public Home addResident(String userAlias) {
        this.getResidents().add(userAlias);
        return this;
    }
}
