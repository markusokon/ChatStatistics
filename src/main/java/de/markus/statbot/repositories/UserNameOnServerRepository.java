package de.markus.statbot.repositories;

import de.markus.statbot.model.UserNameOnServer;
import de.markus.statbot.model.UserNameOnServerPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNameOnServerRepository extends JpaRepository<UserNameOnServer, UserNameOnServerPK> {
}
