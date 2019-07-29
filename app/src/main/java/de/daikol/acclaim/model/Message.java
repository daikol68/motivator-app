package de.daikol.acclaim.model;

import com.fasterxml.jackson.databind.util.StdDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import de.daikol.acclaim.model.user.User;

public class Message implements Serializable {

    private long id;

    private Date creationDate;

    private String text;

    private User sender;

    private User receiver;

    private MessageType type;

    private boolean read;

    private boolean deleted;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public enum MessageType {
        CHAT,
        UPDATE;
    }

    /**
     * This method is used to create a JsonObject out of the reward.
     *
     * @return The JSONObject
     * @throws JSONException If something happens.
     */
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        if (creationDate != null) {
            StdDateFormat format = new StdDateFormat();
            json.put("creationDate", format.format(creationDate));
        }
        json.put("text", text);
        json.put("receiver", receiver.toJson());

        if (sender != null) {
            json.put("sender", sender.toJson());
        }
        if (type != null) {
            json.put("type", type.toString());
        }
        json.put("read", read);
        json.put("deleted", deleted);
        return json;
    }

    /**
     * This method is used to create a reward from a json object.
     *
     * @param json The json object.
     * @return The Reward.
     * @throws JSONException If something happens.
     */
    public static Message fromJson(JSONObject json) throws JSONException, ParseException {
        Message message = new Message();
        message.setId(json.getLong("id"));
        if (json.has("creationDate") && !json.isNull("creationDate")) {
            StdDateFormat format = new StdDateFormat();
            message.setCreationDate(format.parse(json.getString("creationDate")));
        }
        message.setText(json.getString("text"));
        message.setReceiver(User.fromJson(json.getJSONObject("receiver")));

        if (json.has("fromUser") && !json.isNull("sender")) {
            message.setSender(User.fromJson(json.getJSONObject("sender")));
        }
        if (json.has("type") && !json.isNull("type")) {
            message.setType(MessageType.valueOf(json.getString("type")));
        }
        message.setRead(json.getBoolean("read"));
        message.setDeleted(json.getBoolean("deleted"));
        return message;
    }

}
