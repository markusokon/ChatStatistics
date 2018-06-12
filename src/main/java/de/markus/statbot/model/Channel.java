package de.markus.statbot.model;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
public class Channel {

    @Id
    private Long channel_Id;
    private String name;

    @OneToMany(mappedBy = "channel_Id")
    private List<Message> messages;

    @ManyToOne
    @JoinColumn(name = "server_Id", updatable = false)
    private Server server;

    protected Channel() {
    }

    public Channel(Long channel_Id, String name, Server server) {
        this.channel_Id = channel_Id;
        this.name = name;
        this.server = server;
    }

    public void setName(String name) {
        this.name = name;
    }

}
