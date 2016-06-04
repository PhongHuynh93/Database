package dhbk.android.database.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
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

    // field
    private static DatabaseUserHelper sInstance;
    private static Context mContext;

    // use this method to access to database
    public static synchronized DatabaseUserHelper getInstance(Context context) {
        if (sInstance == null) {
            mContext = context.getApplicationContext();
            sInstance = new DatabaseUserHelper(mContext);
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
        new AddUserTask(user).execute();
    }


    public void getUserFromDatabase() {

    }


    // add useracount to database and return status
    private static class AddUserTask extends AsyncTask<Void,Void, Boolean> {
        private User mUser;

        private AddUserTask(User user) {
            this.mUser = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                SQLiteDatabase db = DatabaseUserHelper.getInstance(mContext).getWritableDatabase();
                try {
                    db.beginTransaction();

                    ContentValues userAccount = new ContentValues();
                    userAccount.put(KEY_USER_NAME, mUser.getUserName());
                    userAccount.put(KEY_USER_PASS, mUser.getUserPass());
                    userAccount.put(KEY_USER_EMAIL, mUser.getUserEmail());

                    db.insertOrThrow(TABLE_USERS, null, userAccount);
                    db.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    throw new SQLiteException(e.toString());
                } finally {
                    db.endTransaction();
                }
            } catch (SQLiteException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean error) {
            super.onPostExecute(error);
            if (!error) {
                Log.d(TAG, "Error when add database");
            }
        }
    }
}
