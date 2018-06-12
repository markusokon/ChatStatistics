package de.markus.statbot.model;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Server {

    @Id
    private Long server_Id;

    @ManyToMany
    @JoinColumn(referencedColumnName = "servers")
    private List<User> users;

    private String name;

    protected Server() {
    }

    public Server(Long server_Id, String name, @Nullable List<User> users) {
        this.server_Id = server_Id;
        this.name = name;
        this.users = users;
    }
}
