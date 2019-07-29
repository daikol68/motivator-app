package de.daikol.acclaim.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.daikol.acclaim.util.BitmapUtility;

public class AchievementProgressStep implements Serializable {

    private long id;

    private long achievementId;

    private long userId;

    private Date creationDate;

    private ApplyStatus applied;

    private Date appliedDate;

    private String appliedComment;

    public AchievementProgressStep() {
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

    public long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(long achievementId) {
        this.achievementId = achievementId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public ApplyStatus getApplied() {
        return applied;
    }

    public void setApplied(ApplyStatus applied) {
        this.applied = applied;
    }

    public Date getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Date appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getAppliedComment() {
        return appliedComment;
    }

    public void setAppliedComment(String appliedComment) {
        this.appliedComment = appliedComment;
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
        json.put("userId", userId);
        json.put("achievementId", achievementId);
        if (creationDate != null) {
            json.put("creationDate", creationDate.getTime());
        }
        if (applied != null) {
            json.put("applied", applied.toString());
        }
        if (appliedDate != null) {
            json.put("appliedDate", appliedDate.getTime());
        }
        if (appliedComment != null) {
            json.put("appliedComment", appliedComment);
        }
        return json;
    }

    /**
     * This method is used to create a reward from a json object.
     *
     * @param json The json object.
     * @return The Reward.
     * @throws JSONException If something happens.
     */
    public static AchievementProgressStep fromJson(JSONObject json) throws JSONException {
        AchievementProgressStep achievementProgressStep = new AchievementProgressStep();
        achievementProgressStep.setId(json.getLong("id"));
        achievementProgressStep.setUserId(json.getLong("userId"));
        achievementProgressStep.setAchievementId(json.getLong("achievementId"));

        if (json.has("creationDate")) {
            achievementProgressStep.setCreationDate(new Date(json.getLong("creationDate")));
        }
        if (json.has("applied")) {
            achievementProgressStep.setApplied(ApplyStatus.valueOf(json.getString("applied")));
        }
        if (json.has("appliedDate")) {
            achievementProgressStep.setAppliedDate(new Date(json.getLong("appliedDate")));
        }
        if (json.has("appliedComment")) {
            achievementProgressStep.setAppliedComment(json.getString("appliedComment"));
        }

        return achievementProgressStep;
    }

}