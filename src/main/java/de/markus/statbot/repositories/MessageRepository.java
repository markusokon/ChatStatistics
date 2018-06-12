package de.markus.statbot.repositories;

import de.markus.statbot.model.Message;
import de.markus.statbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Double countByAuthor(User author);

    @Query("select max(m.creationDate) from Message m")
    Date findByMaxCreationDate();
}
