package de.daikol.acclaim.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class ProgressStepData implements Serializable, Bitmapable {

    private static final String LOG_TAG = "ProgressStepData";

    private long id;

    private Date creationDate;

    private long userid;

    private long competition;

    private String achievementName;

    private byte[] achievementPicture;

    @JsonIgnore
    private transient Bitmap bitmap;

    private ApplyStatus applied;

    private Date appliedDate;

    private String appliedComment;

    public ProgressStepData() {
        // nothing to do
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public long getCompetition() {
        return competition;
    }

    public void setCompetition(long competition) {
        this.competition = competition;
    }

    public String getAchievementName() {
        return achievementName;
    }

    public void setAchievementName(String achievementName) {
        this.achievementName = achievementName;
    }

    public byte[] getAchievementPicture() {
        return achievementPicture;
    }

    public void setAchievementPicture(byte[] achievementPicture) {
        this.achievementPicture = achievementPicture;
    }

    @JsonIgnore
    @Override
    public byte[] getPicture() {
        return achievementPicture;
    }

    @JsonIgnore
    @Override
    public void setPicture(byte[] achievementPicture) {
        this.achievementPicture = achievementPicture;
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