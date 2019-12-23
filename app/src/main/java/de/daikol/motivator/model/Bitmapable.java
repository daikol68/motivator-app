package de.daikol.motivator.model;

import android.graphics.Bitmap;

public interface Bitmapable {

    Bitmap getBitmap();

    void setBitmap(Bitmap bitmap);

    byte[] getPicture();

    void setPicture(byte[] picture);

}
