package de.markus.statbot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    private Long id;

    private String globalName;

    @ManyToMany
    private List<Server> server;

    @OneToMany
    private List<Message> messages;

    protected User() {
    }

    public User(Long id) {
        this.id = id;
    }

}