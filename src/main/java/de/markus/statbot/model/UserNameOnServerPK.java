package de.markus.statbot.model;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
public class UserNameOnServerPK {

    @Column(nullable = false)
    private Long serverId;

    @Column(nullable = false)
    private Long userId;

    protected UserNameOnServerPK() {
    }

    public UserNameOnServerPK(Long serverId, Long userId) {
        this.serverId = serverId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserNameOnServerPK)) {
            return false;
        }
        UserNameOnServerPK castOther = (UserNameOnServerPK) other;
        return
                (this.serverId.equals(castOther.serverId))
                        && this.userId.equals(castOther.userId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.serverId.hashCode();
        hash = hash * prime + this.userId.hashCode();
        return hash;
    }

}
