package de.markus.statbot.model;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Getter
public class Message {

    @Id
    private Long id;
    private Integer length;
    private Date creationDate;

    @ManyToOne
    private User author;

    protected Message() {
    }

    public Message(Long id, Integer length, User author, Date creationDate) {
        this.id = id;
        this.length = length;
        this.author = author;
        this.creationDate = creationDate;
    }
}
