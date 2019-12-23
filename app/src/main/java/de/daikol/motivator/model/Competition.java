package de.daikol.motivator.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Competition implements Serializable, Cloneable, Bitmapable {

    private static final String LOG_TAG = "Competition";

    private long id;

    private Date creationDate;

    private String name;

    private byte[] picture;

    @JsonIgnore
    private transient Bitmap bitmap;

    private List<Competitor> competitors;

    private List<Achievement> achievements;

    private List<Reward> rewards;

    public Competition() {
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

    public List<Competitor> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(List<Competitor> competitors) {
        this.competitors = competitors;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
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

    public Competition clone() {
        Competition competition = new Competition();
        competition.setName(name);
        competition.setCreationDate(creationDate);
        competition.setBitmap(bitmap);
        competition.setPicture(picture);
        competition.setCompetitors(new ArrayList<>(competitors));
        competition.setAchievements(new ArrayList<>(achievements));
        competition.setRewards(new ArrayList<>(rewards));
        return competition;
    }

    public Competitor findCompetitorByUserId(long userId) {
        if (this.competitors != null) {
            for (Competitor competitor : this.competitors) {
                if (competitor.getUser().getId() == userId) {
                    return competitor;
                }
            }
        }
        return null;
    }

    public boolean isSoloByUserId(long userId) {
        boolean isSolo = true;
        if (this.competitors != null) {
            for (Competitor competitor : this.competitors) {
                if (competitor.getUser().getId() == userId) {
                    continue;
                }
                if (competitor.getStatus() == CompetitionStatus.CONFIRMED) {
                    isSolo = false;
                }
            }
        }
        return isSolo;
    }
}
