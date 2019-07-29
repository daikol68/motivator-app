package de.daikol.acclaim.model.user;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;

import de.daikol.acclaim.model.Bitmapable;
import de.daikol.acclaim.util.BitmapUtility;

public class User implements Serializable, Bitmapable {

    private long id;

    private String name;

    private String code;

    private String password;

    private byte[] picture;

    private String email;

    private boolean registration;

    @JsonIgnore
    private transient Bitmap bitmap;

    private List<Role> roles;

    public User() {
        // nothing to do
    }

    public User(long id, String name, String password, String email, byte[] picture, Bitmap bitmap) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.picture = picture;
        this.bitmap = bitmap;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRegistration() {
        return registration;
    }

    public void setRegistration(boolean registration) {
        this.registration = registration;
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * This method is used to create a JsonObject.
     *
     * @return The JSONObject
     * @throws JSONException If something happens.
     */
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("password", password);
        json.put("email", email);
        if (picture != null) {
            json.put("picture", picture);
        }
        if (bitmap != null) {
            json.put("bitmap", BitmapUtility.getStringFromBitmap(bitmap));
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
    public static User fromJson(JSONObject json) throws JSONException {
        User user = new User();
        user.setId(json.getLong("id"));
        user.setName(json.getString("name"));
        if (json.has("password")) {
            user.setPassword(json.getString("password"));
        }
        if (json.has("email")) {
            user.setEmail(json.getString("email"));
        }
        if (json.has("picture")) {
            user.setPicture(json.getString("picture").getBytes(Charset.forName("UTF-8")));
        }
        if (json.has("bitmap")) {
            user.setBitmap(BitmapUtility.getBitmapFromString(json.getString("bitmap")));
        }
        return user;
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
        } else {
            oos.writeObject(new byte[0]);
        }
        if (picture != null) {
            oos.writeObject(picture);
        } else {
            oos.writeObject(new byte[0]);
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Now, all again, deserializing - in the SAME ORDER!
        // All non-transient fields
        ois.defaultReadObject();
        byte[] picture = (byte[]) ois.readObject();
        if (picture != null && picture.length > 0) {
            this.picture = picture;
        }
        // All other fields that you serialized
        byte[] image = (byte[]) ois.readObject();
        if (image != null && image.length > 0) {
            this.bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }
}