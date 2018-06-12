package de.markus.statbot.model;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
public class UserNameOnServerPK {

    private Long server_Id_FK;

    private Long user_Id_FK;

    protected UserNameOnServerPK() {
    }

    public UserNameOnServerPK(Long server_Id_FK, Long user_Id_FK) {
        this.server_Id_FK = server_Id_FK;
        this.user_Id_FK = user_Id_FK;
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
                (this.server_Id_FK.equals(castOther.server_Id_FK))
                        && this.user_Id_FK.equals(castOther.user_Id_FK);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.server_Id_FK.hashCode();
        hash = hash * prime + this.user_Id_FK.hashCode();
        return hash;
    }

}
