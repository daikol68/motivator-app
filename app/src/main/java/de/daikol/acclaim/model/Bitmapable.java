package de.daikol.acclaim.model;

import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Bitmapable {

    Bitmap getBitmap();

    void setBitmap(Bitmap bitmap);

    byte[] getPicture();

    void setPicture(byte[] picture);

}
