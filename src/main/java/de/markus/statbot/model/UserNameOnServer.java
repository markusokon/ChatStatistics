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
    private UserNameOnServerPK userNameOnServer_id;

    private String name;

    protected UserNameOnServer() {
    }

    public UserNameOnServer(UserNameOnServerPK userNameOnServer_id, String name) {
        this.userNameOnServer_id = userNameOnServer_id;
        this.name = name;
    }
}
