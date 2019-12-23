package de.daikol.motivator.model;


import java.io.Serializable;
import java.util.Date;

public class RewardProgressStep implements Serializable {

    private long id;

    private long userId;

    private Date creationDate;

    public RewardProgressStep() {
        // nothing to do
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


}