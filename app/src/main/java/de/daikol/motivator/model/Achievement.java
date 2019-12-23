package de.daikol.motivator.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.daikol.motivator.util.BitmapUtility;

public class Achievement implements Serializable, Cloneable, Bitmapable {

    private static final String LOG_TAG = "Achievement";

    private long id;

    private Date creationDate;

    private String name;

    private String description;

    private byte[] picture;

    @JsonIgnore
    private transient Bitmap bitmap;

    private AchievementStatus status;

    private int points;

    private int progressStepsFinish;

    private AchievementReviewType type;

    private List<AchievementProgressStep> progressSteps;

    public Achievement() {
        // nothing to do
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    @JsonIgnore
    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @JsonIgnore
    @Override
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public AchievementStatus getStatus() {
        return status;
    }

    public void setStatus(AchievementStatus status) {
        this.status = status;
    }

    public AchievementReviewType getType() {
        return type;
    }

    public void setType(AchievementReviewType type) {
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<AchievementProgressStep> getProgressSteps() {
        return progressSteps;
    }

    public void setProgressSteps(List<AchievementProgressStep> progressSteps) {
        this.progressSteps = progressSteps;
    }

    public int getProgressStepsFinish() {
        return progressStepsFinish;
    }

    public void setProgressStepsFinish(int progressStepsCount) {
        this.progressStepsFinish = progressStepsCount;
    }

    @Override
    public Achievement clone() {
        Achievement clone = new Achievement();
        clone.setId(getId());
        clone.setCreationDate(getCreationDate());
        clone.setName(getName());
        clone.setDescription(getDescription());
        clone.setPoints(getPoints());
        clone.setPicture(getPicture());
        clone.setStatus(getStatus());
        clone.setType(getType());
        clone.setProgressStepsFinish(getProgressStepsFinish());
        return clone;
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
            json.put("creationDate", creationDate.getTime());
        }
        json.put("name", name);
        if (description != null) {
            json.put("description", description);
        }
        if (picture != null) {
            json.put("picture", picture);
        }
        if (bitmap != null) {
            json.put("bitmap", BitmapUtility.getStringFromBitmap(bitmap));
        }
        if (status != null) {
            json.put("status", status.toString());
        }
        if (type != null) {
            json.put("type", type.toString());
        }

        json.put("points", points);
        if (progressSteps != null) {
            List<JSONObject> objectList = new ArrayList<>();
            for (AchievementProgressStep progressStep : progressSteps) {
                objectList.add(progressStep.toJson());
            }
            JSONArray progressStepsArray = new JSONArray(objectList);
            json.put("progressSteps", progressStepsArray);
        }
        json.put("progressStepsFinish", progressStepsFinish);
        return json;
    }

    /**
     * This method is used to create a reward from a json object.
     *
     * @param json The json object.
     * @return The Reward.
     * @throws JSONException If something happens.
     */
    public static Achievement fromJson(JSONObject json) throws JSONException {
        Achievement achievement = new Achievement();
        achievement.setId(json.getLong("id"));
        if (json.has("creationDate")) {
            achievement.setCreationDate(new Date(json.getLong("creationDate")));
        }
        achievement.setName(json.getString("name"));
        if (json.has("description")) {
            achievement.setDescription(json.getString("description"));
        }
        if (json.has("picture")) {
            achievement.setPicture(json.getString("picture").getBytes(Charset.forName("UTF-8")));
        }
        if (json.has("status")) {
            achievement.setStatus(AchievementStatus.valueOf(json.getString("status")));
        }
        if (json.has("type")) {
            achievement.setType(AchievementReviewType.valueOf(json.getString("type")));
        }
        achievement.setPoints(json.getInt("points"));
        if (json.has("progressSteps")) {
            List<AchievementProgressStep> progressSteps = new ArrayList<>();
            JSONArray array = json.getJSONArray("progressSteps");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                AchievementProgressStep progressStep = AchievementProgressStep.fromJson(jsonObject);
                progressSteps.add(progressStep);
            }
            achievement.setProgressSteps(progressSteps);
        }
        achievement.setProgressStepsFinish(json.getInt("progressStepsFinish"));
        if (json.has("bitmap")) {
            achievement.setBitmap(BitmapUtility.getBitmapFromString(json.getString("bitmap")));
        }
        return achievement;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        // This will serialize all fields that you did not mark with 'transient'
        // (Java's default behaviour)
        oos.defaultWriteObject();
        // Now, manually serialize all transient fields that you want to be serialized
        if (bitmap != null) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            boolean success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            if (success) {
                oos.writeObject(byteStream.toByteArray());
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Now, all again, deserializing - in the SAME ORDER!
        // All non-transient fields
        ois.defaultReadObject();
        // All other fields that you serialized

        // All other fields that you serialized
        try {
            byte[] image = (byte[]) ois.readObject();
            if (image != null && image.length > 0) {
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
        } catch (IOException e) {
            Log.w(LOG_TAG, "Exception raised while reading object due to " + e.getMessage() + ".");
        }
    }

}
