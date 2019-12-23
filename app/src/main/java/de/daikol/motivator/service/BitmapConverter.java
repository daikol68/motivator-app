package de.daikol.motivator.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import de.daikol.motivator.R;
import de.daikol.motivator.util.BitmapUtility;

/**
 * This class is a utility used for converting bitmaps. This class does also use a cache for the bitmaps.
 */
public class BitmapConverter {

    /**
     * The tag used for logging.
     */
    private static final String LOG_TAG = "BitmapConverter";

    /**
     * This method is used to convert a bitmap.
     *
     * @param image   The url to download the bitmap from.
     * @param context The context.
     * @return The Bitmap.
     */
    public static Bitmap convertBitmap(byte[] image, Context context) {

        // check url and return null if url is empty
        Bitmap bitmap = null;
        if (image == null) {
            return bitmap;
        }

        // try to load from the cache and return if a bitmap exists
        bitmap = BitmapCache.getBitmap(image);
        if (bitmap != null) {
            return bitmap;
        }

        try {
            InputStream is = new ByteArrayInputStream(image);
            bitmap = BitmapFactory.decodeStream(is);
            bitmap = BitmapUtility.scaleDownBitmap(bitmap, BitmapUtility.HEIGHT, context);
            BitmapCache.addBitmap(image, bitmap);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception [{}] raised while downloading bitmap from source [" + image + "] with message [" + e.getMessage() + "]");
            bitmap = drawableToBitmap(context.getResources().getDrawable(R.drawable.ic_unknown));
            BitmapCache.addBitmap(image, bitmap);
        }

        return bitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
