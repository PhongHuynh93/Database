package dhbk.android.database.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by huynhducthanhphong on 6/4/16.
 */
public class DatabaseUserHelper extends SQLiteOpenHelper{
    // database
    private static final String DATABASE_NAME = "yaho";
    private static final int DATABASE_VERSION = 1;

    // table
    private static final String TABLE_USERS = "user_accounts";
    private static final String TABLE_POSTS = "user_posts";

    // user accounts Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";

    // user posts Table Columns, const thứ 2 là tham số liên kết với table "account"
    private static final String KEY_POST_ID = "id";
    private static final String KEY_POST_USER_ID_FK = "userId";
    private static final String KEY_POST_TEXT = "text";


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
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_PROFILE_PICTURE_URL + " TEXT" +
                ")";


        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS +
                "(" +
                KEY_POST_ID + " INTEGER PRIMARY KEY" + "," +// Define a primary key
                KEY_POST_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_POST_TEXT + " TEXT" +
                ")";

        db.execSQL(CREATE_POSTS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
    }


}
