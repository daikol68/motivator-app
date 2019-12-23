package de.daikol.motivator.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.provider.BaseColumns;

import de.daikol.motivator.model.user.User;
import de.daikol.motivator.util.BitmapUtility;

/**
 * This class is used to create the database for the user.
 */
public class UserHelper extends SQLiteOpenHelper {

    /**
     * The tag used for logging.
     */
    private static final String LOG_TAG = "UserHelper";

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PICTURE = "picture";
        public static final String COLUMN_NAME_BITMAP = "bitmap";
    }

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "User.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_NAME + " TEXT," +
                    FeedEntry.COLUMN_NAME_PASSWORD + " TEXT," +
                    FeedEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    FeedEntry.COLUMN_NAME_PICTURE + " TEXT," +
                    FeedEntry.COLUMN_NAME_BITMAP + " BLOB)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;


    public UserHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    /**
     * This method deletes the user that exists in the database.
     */
    public void deleteUser() {
        SQLiteDatabase db = getWritableDatabase();

        try {
            // Issue SQL statement.
            db.delete(FeedEntry.TABLE_NAME, null, null);
        } finally {
            //db.close();
        }
    }

    /**
     * This method is used to persist the user.
     *
     * @param user The user.
     */
    public void persistUser(User user) {

        SQLiteDatabase db = getWritableDatabase();

        try {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(FeedEntry.COLUMN_NAME_ID, user.getId());
            values.put(FeedEntry.COLUMN_NAME_NAME, user.getName());
            values.put(FeedEntry.COLUMN_NAME_PASSWORD, user.getPassword());
            values.put(FeedEntry.COLUMN_NAME_EMAIL, user.getEmail());
            values.put(FeedEntry.COLUMN_NAME_PICTURE, user.getPicture());
            values.put(FeedEntry.COLUMN_NAME_BITMAP, BitmapUtility.getBytes(user.getBitmap()));

            // Insert the new row, returning the primary key value of the new row
            db.insert(UserHelper.FeedEntry.TABLE_NAME, null, values);
        } finally {
            //db.close();
        }

    }

    /**
     * This method is used to fetch the user from the local database.
     *
     * @return The User or null if none exists.
     */
    public User fetchUser() {

        // define which columns to return
        String[] projection = {
                FeedEntry.COLUMN_NAME_ID,
                FeedEntry.COLUMN_NAME_NAME,
                FeedEntry.COLUMN_NAME_PASSWORD,
                FeedEntry.COLUMN_NAME_EMAIL,
                FeedEntry.COLUMN_NAME_PICTURE,
                FeedEntry.COLUMN_NAME_BITMAP
        };

        // get the database
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            // get the cursor for the data
            cursor = db.query(
                    FeedEntry.TABLE_NAME,        // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // don't sort the values
            );

            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_NAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_PASSWORD));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_EMAIL));
                byte[] picture = cursor.getBlob(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_PICTURE));
                Bitmap bitmap = BitmapUtility.convertBitmap(cursor.getBlob(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_BITMAP)));
                return new User(id, name, password, email, picture, bitmap);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            //db.close();
        }

        return null;
    }


}
