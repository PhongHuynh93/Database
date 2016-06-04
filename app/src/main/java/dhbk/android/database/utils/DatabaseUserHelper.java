package dhbk.android.database.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import dhbk.android.database.R;
import dhbk.android.database.models.User;

/**
 * Created by huynhducthanhphong on 6/4/16.
 */
public class DatabaseUserHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseUserHelper.class.getSimpleName();

    // database
    private static final String DATABASE_NAME = "yaho";
    private static final int DATABASE_VERSION = 2;

    // table
    private static final String TABLE_USERS = "user_accounts";
    private static final String TABLE_POSTS = "user_posts";

    // user accounts Table Columns
    private static final String KEY_USER_ID = "_id";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PASS = "userPass";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";

    // user posts Table Columns, const thứ 2 là tham số liên kết với table "account"
    private static final String KEY_POST_ID = "_id";
    private static final String KEY_POST_USER_ID_FK = "userId";
    private static final String KEY_POST_TEXT = "text";

    // create table user accounts
    private static final String KEY_CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS +
            "(" +
            KEY_USER_ID + " INTEGER PRIMARY KEY," +
            KEY_USER_NAME + " TEXT NOT NULL," +
            KEY_USER_EMAIL + " TEXT NOT NULL UNIQUE," +
            KEY_USER_PASS + " TEXT NOT NULL," +
            KEY_USER_PROFILE_PICTURE_URL + " INT DEFAULT " + R.mipmap.ic_launcher +
            ")";

    // create table user posts
    private static final String KEY_CREATE_POST_TABLE = "CREATE TABLE " + TABLE_POSTS +
            "(" +
            KEY_POST_ID + " INTEGER PRIMARY KEY" + "," +// Define a primary key
            KEY_POST_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
            KEY_POST_TEXT + " TEXT" +
            ")";

    private static DatabaseUserHelper sInstance;

    // use this method to access to database
    public static synchronized DatabaseUserHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseUserHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    // make database
    private DatabaseUserHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(KEY_CREATE_USER_TABLE);
        db.execSQL(KEY_CREATE_POST_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }


    public void addUserToDatabase(User user) {
        // TODO: 6/4/16 add to database in asynctask
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, user.getUserName());
            values.put(KEY_USER_PASS, user.getUserPass());
            values.put(KEY_USER_EMAIL, user.getUserEmail());

            db.insertOrThrow(TABLE_USERS, null, values);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }
}
