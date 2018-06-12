package de.markus.statbot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    @Column(unique = true, nullable = false)
    private Long user_Id;

    private String globalName;

    @ManyToMany
    private List<Server> servers;

    @OneToMany(mappedBy = "author")
    private List<Message> messages;

    protected User() {
    }

    public User(Long user_Id, String globalName) {
        this.user_Id = user_Id;
        this.globalName = globalName;
    }

}