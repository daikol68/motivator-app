package de.daikol.motivator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import de.daikol.motivator.model.user.User;

public class Competitor implements Serializable {

    private long id;

    private int points;

    private CompetitionStatus status;

    private Competition competition;

    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public CompetitionStatus getStatus() {
        return status;
    }

    public void setStatus(CompetitionStatus status) {
        this.status = status;
    }

    @JsonIgnore
    public Competition getCompetition() {
        return competition;
    }

    @JsonIgnore
    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        json.put("points", points);
        if (status != null) {
            json.put("status", status.toString());
        }
        json.put("user", user.toJson());
        return json;
    }

    /**
     * This method is used to create a reward from a json object.
     *
     * @param json The json object.
     * @return The Reward.
     * @throws JSONException If something happens.
     */
    public static Competitor fromJson(JSONObject json, Competition competition) throws JSONException {
        Competitor competitor = new Competitor();
        competitor.setId(json.getLong("id"));
        competitor.setPoints(json.getInt("points"));
        if (json.has("status")) {
            competitor.setStatus(CompetitionStatus.valueOf(json.getString("status")));
        }
        competitor.setUser(User.fromJson(json.getJSONObject("user")));
        competitor.setCompetition(competition);
        return competitor;
    }
}
