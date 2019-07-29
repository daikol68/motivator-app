package de.daikol.acclaim.model.user;

import java.io.Serializable;

public class Role implements Serializable {

    private long id;

    private Type type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        /**
         * The admin user.
         */
        ADMIN,
        /**
         * The user.
         */
        USER;
    }

}
