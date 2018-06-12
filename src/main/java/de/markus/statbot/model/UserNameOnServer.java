package de.markus.statbot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class UserNameOnServer {

    @EmbeddedId
    private UserNameOnServerPK id;

    private String name;

    protected UserNameOnServer() {
    }

    public UserNameOnServer(UserNameOnServerPK id, String name) {
        this.id = id;
        this.name = name;
    }
}
