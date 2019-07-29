package de.daikol.acclaim.service;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by daikol on 15.12.2016.
 */
public class BitmapCache {

    /**
     * The tag used for logging.
     */
    private static final String LOG_TAG = "BitmapCache";

    /**
     * The singleton instance.
     */
    private static BitmapCache INSTANCE = null;

    private LruCache<byte[], Bitmap> mMemoryCache;

    private BitmapCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<byte[], Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(byte[] key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static synchronized BitmapCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BitmapCache();
        }
        return INSTANCE;
    }

    public void addBitmapToCache(byte[] key, Bitmap bitmap) {
        this.mMemoryCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromCache(byte[] key) {
        return mMemoryCache.get(key);
    }

    public static void addBitmap(byte[] key, Bitmap bitmap) {
        getInstance().addBitmapToCache(key, bitmap);
    }

    public static Bitmap getBitmap(byte[] key) {
        return getInstance().getBitmapFromCache(key);
    }

}
