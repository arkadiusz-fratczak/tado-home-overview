package tado.homeoverview.home;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tado.homeoverview.home.domain.Home;

import java.util.List;

public interface HomeRepository extends JpaRepository<Home, Long> {

    @Query("SELECT h FROM Home h JOIN h.residents r WHERE r = :userAlias")
    List<Home> findAllByResident(@Param("userAlias") String userAlias);
}
