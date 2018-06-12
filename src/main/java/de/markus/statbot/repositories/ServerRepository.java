package de.markus.statbot.repositories;

import de.markus.statbot.model.Server;
import de.markus.statbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Long> {

    List<Server> findAllByUsersContaining(List<User> users);

}
