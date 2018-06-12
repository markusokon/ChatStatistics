package de.markus.statbot.model;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
@Getter
@Setter
public class Server {

    @Id
    private Long Id;

    @ManyToMany
    private List<User> users;

    private String name;

    protected Server() {
    }

    public Server(Long Id, String name, @Nullable List<User> users) {
        this.Id = Id;
        this.name = name;
        this.users = users;
    }
}
