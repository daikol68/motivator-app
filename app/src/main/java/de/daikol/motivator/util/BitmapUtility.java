package de.daikol.motivator.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by daikol on 24.12.2016.
 */
public class BitmapUtility {

    // the height that is used for the height
    public static final int HEIGHT = 50;

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap convertBitmap(byte[] image) {
        if (image == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * This method is used to create a string from a bitmap.
     * @param bitmap The bitmap.
     * @return The string.
     */
    public static String getStringFromBitmap(Bitmap bitmap) {
        final int COMPRESSION_QUALITY = 100;
        final ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, byteArrayBitmapStream);
        final byte[] b = byteArrayBitmapStream.toByteArray();
        final String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    /**
     * This method is used to convert a json in a bitmap.
     * @param json The json to be converted.
     * @return The bitmap.
     */
    public static  Bitmap getBitmapFromString(String json) {
        final byte[] decodedString = Base64.decode(json, Base64.DEFAULT);
        final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    /**
     * This method is used to scale down the bitmap.
     * @param bitmap The bitmap that should be scaled down.
     * @param height The height of the bitmap.
     * @param context The context.
     * @return The scaled down bitmap.
     */
    public static Bitmap scaleDownBitmap(Bitmap bitmap, int height, Context context) {
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        int h= (int) (height*densityMultiplier);
        int w= (int) (h * bitmap.getWidth()/((double) bitmap.getHeight()));
        bitmap=Bitmap.createScaledBitmap(bitmap, w, h, true);
        return bitmap;
    }
}