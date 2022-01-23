package tado.homeoverview.home.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class Home {

    public Home(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String owner;

    private String name;

    @ElementCollection
    private Set<String> residents = new HashSet<>();

    @OneToMany(orphanRemoval = true, mappedBy = "home")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
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
