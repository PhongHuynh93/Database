package dhbk.android.database.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import dhbk.android.database.R;
import dhbk.android.database.models.Post;
import dhbk.android.database.models.User;

/**
 * Created by huynhducthanhphong on 6/4/16.
 */
public class DatabaseUserHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseUserHelper.class.getSimpleName();

    // database
    private static final String DATABASE_NAME = "yaho";
    private static final int DATABASE_VERSION = 6;

    // table
    private static final String TABLE_USERS = "user_accounts";

    // user accounts Table Columns
    private static final String KEY_USER_ID = "_id";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PASS = "userPass";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";
    private static final String KEY_USER_HAS_POST_TABLE = "hasPostTable"; // có table Post chưa

    // user posts Table Columns, const thứ 2 là tham số liên kết với table "account"
    private static final String KEY_POST_ID = "_id";
    public static final String KEY_POST_IMAGE = "imageResId";
    public static final String KEY_POST_TITLE_TEXT = "titlePost";
    public static final String KEY_POST_BODY_TEXT = "bodyPost";


    // create table user accounts
    // TODO: 6/5/16 change string to string builder
    private static final String KEY_CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS +
            "(" +
            KEY_USER_ID + " INTEGER PRIMARY KEY," +
            KEY_USER_NAME + " TEXT NOT NULL," +
            KEY_USER_EMAIL + " TEXT NOT NULL UNIQUE," +
            KEY_USER_PASS + " TEXT NOT NULL," +
            KEY_USER_PROFILE_PICTURE_URL + " INT DEFAULT " + R.mipmap.ic_launcher + "," +
            KEY_USER_HAS_POST_TABLE + " INTEGER DEFAULT 0" +
            ")"; // mặc định mỗi user chưa có 1 table của riêng họ -> gán 0 là mặc định

//    // create table user posts
//    private static final String KEY_CREATE_POST_TABLE = "CREATE TABLE " + TABLE_POSTS +
//            "(" +
//            KEY_POST_ID + " INTEGER PRIMARY KEY" + "," +// Define a primary key
//            KEY_POST_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
//            KEY_POST_TEXT + " TEXT" +
//            ")";

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
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }


    public void addUserToDatabase(@NonNull User user) {
        new AddUserTask().execute(user.getUserName(), user.getUserEmail(), user.getUserPass());
    }


    // because email id unique, so we user email to get userAccount
    @Nullable
    public User getUserFromDatabase(String email) {
        User userAccount = null;
        try {
            SQLiteDatabase db = getReadableDatabase();

            Cursor userAccountCursor = db.query(
                    TABLE_USERS,
                    new String[]{KEY_USER_NAME, KEY_USER_EMAIL, KEY_USER_PASS, KEY_USER_PROFILE_PICTURE_URL, KEY_USER_HAS_POST_TABLE},
                    KEY_USER_EMAIL + " = ?",
                    new String[]{email},
                    null,
                    null,
                    null
            );

            if (userAccountCursor != null && userAccountCursor.getCount() > 0 && userAccountCursor.moveToFirst()) {
                userAccount = new User(
                        userAccountCursor.getString(0),
                        userAccountCursor.getString(1),
                        userAccountCursor.getString(2),
                        userAccountCursor.getInt(3),
                        userAccountCursor.getInt(4)
                );
            }

            if (userAccountCursor != null) {
                userAccountCursor.close();
            }

        } catch (SQLiteException e) {
            Log.d(TAG, "Can get user account");
        }

        return userAccount;
    }


    // tạo table post user với tên là email
    public void createPostTable(String email) {
        final String KEY_CREATE_POST_TABLE = "CREATE TABLE " + removeSpecialChar(email) +
                "(" +
                KEY_POST_ID + " INTEGER PRIMARY KEY," +
                KEY_POST_IMAGE + " INTEGER NOT NULL" +
                KEY_POST_TITLE_TEXT + " TEXT NOT NULL," +
                KEY_POST_BODY_TEXT + " TEXT NOT NULL," +
                ")";
        try {
            SQLiteDatabase db = DatabaseUserHelper.getInstance(mContext).getWritableDatabase();
            updateSQL(email);
            db.execSQL(KEY_CREATE_POST_TABLE);
        } catch (SQLiteException e) {
            Log.e(TAG, "createPostTable: " + e.toString());
        }

    }

    // update column hasPost table User
    public void updateSQL(String email) {
        try {
            ContentValues drinkValues = new ContentValues();
            drinkValues.put(KEY_USER_HAS_POST_TABLE, 1);
            SQLiteDatabase db = DatabaseUserHelper.getInstance(mContext).getWritableDatabase();
            db.update(TABLE_USERS, drinkValues,KEY_USER_EMAIL + " = ?",new String[] {email});
        } catch (SQLiteException e) {
            Log.e(TAG, "updateSQL: " + e.toString() );
        }

    }

    public void makeMessageInPostTable(String namePostTable, int imgId, String mesTitle, String mesBody) {
        Post post = new Post(namePostTable, imgId, mesTitle, mesBody);
        // TODO: 6/7/2016 add to post table by calling asynctask
        new AddPostTask().execute(post);
    }

    public static String removeSpecialChar(String email) {
        email = email.replace("@", "");
        email = email.replace(".", "");
        return email;
    }

    // get table Post including image and title text,
    @Nullable
    public void queryPostTable(Context activityContext, String postTableName) {
        new GetCursorTablePost(activityContext).execute(postTableName);
    }


    // add useracount to database and return status
    private static class AddUserTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            if (params.length != 3) {
                return false;
            }
            try {
                SQLiteDatabase db = DatabaseUserHelper.getInstance(mContext).getWritableDatabase();
                try {
                    db.beginTransaction();

                    ContentValues userAccount = new ContentValues();
                    userAccount.put(KEY_USER_NAME, params[0]);
                    userAccount.put(KEY_USER_EMAIL, params[1]);
                    userAccount.put(KEY_USER_PASS, params[2]);

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

    // add useracount to database and return status
    private static class AddPostTask extends AsyncTask<Post, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Post... params) {
            if (params.length != 1) {
                return false;
            }
            try {
                SQLiteDatabase db = DatabaseUserHelper.getInstance(mContext).getWritableDatabase();
                try {
                    db.beginTransaction();

                    ContentValues userPost = new ContentValues();
                    userPost.put(KEY_POST_IMAGE, params[0].getMesId());
                    userPost.put(KEY_POST_TITLE_TEXT, params[0].getTextTitle());
                    userPost.put(KEY_POST_BODY_TEXT, params[0].getTextBody());

                    db.insertOrThrow(params[0].getNamePostable(), null, userPost);
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

    private static class GetCursorTablePost extends AsyncTask<String, Void, Cursor> {
        private final Context mActivityContext;
        private OnDatabaseInteractionListener mOnDatabaseInteractionListener;
        private GetCursorTablePost(Context activityContext) {
            mActivityContext = activityContext;
        }
        @Override
        protected Cursor doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }

            String namePostTable = params[0];
            namePostTable = removeSpecialChar(namePostTable);
            Cursor postCursor;
            try {
                SQLiteDatabase db = DatabaseUserHelper.getInstance(mContext).getWritableDatabase();
                postCursor = db.query(namePostTable,
                        new String[] {KEY_POST_IMAGE, KEY_POST_TITLE_TEXT},
                        null, null, null, null, null);
                return postCursor;
            } catch (SQLiteException e) {
                Log.e(TAG, "createPostTable: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            if (mActivityContext instanceof OnDatabaseInteractionListener) {
                mOnDatabaseInteractionListener = (OnDatabaseInteractionListener)mActivityContext;
                mOnDatabaseInteractionListener.onResultPostTable(cursor);
            }
        }
    }

    // help to interact with database
    public interface OnDatabaseInteractionListener {
        void onResultPostTable(Cursor result); // this method'll return cursor after query post table.
    }
}
