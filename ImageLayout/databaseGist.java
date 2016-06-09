
###########################################################################
// get object from cursor from database, must be in asynctask -> use interface to return
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

###########################################################################
// create table
    // tạo table post user với tên là email
    public void createPostTable(String email) {
        final String KEY_CREATE_POST_TABLE = "CREATE TABLE " + removeSpecialChar(email) +
                "(" +
                KEY_POST_ID + " INTEGER PRIMARY KEY," +
                KEY_POST_IMAGE + " INTEGER NOT NULL," +
                KEY_POST_TITLE_TEXT + " TEXT NOT NULL," +
                KEY_POST_BODY_TEXT + " TEXT NOT NULL" +
                ")";
        try {
            SQLiteDatabase db = DatabaseUserHelper.getInstance(mContext).getWritableDatabase();
            updateSQL(email);
            db.execSQL(KEY_CREATE_POST_TABLE);
        } catch (SQLiteException e) {
            Log.e(TAG, "createPostTable: " + e.toString());
        }

    }

###########################################################################
// update column hasPost table User, because has getWritableDatabase -> must inplement in asynctask
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

###########################################################################
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

                    db.insertOrThrow(removeSpecialChar(params[0].getNamePostable()), null, userPost);
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

###########################################################################
    // update userpost to database and return status
    private static class UpdatePostTask extends AsyncTask<Post, Void, Boolean> {
        private int mPosition;
        private UpdatePostTask(int position) {
            this.mPosition = position;
        }

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
                    userPost.put(KEY_POST_TITLE_TEXT, params[0].getTextTitle());
                    userPost.put(KEY_POST_BODY_TEXT, params[0].getTextBody());

                    db.update(removeSpecialChar(params[0].getNamePostable()), userPost, KEY_POST_ID + " = ?",new String[] {Integer.toString(mPosition)});
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

###########################################################################
// query database
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
                        new String[] {KEY_POST_ID, KEY_POST_IMAGE, KEY_POST_TITLE_TEXT, KEY_POST_BODY_TEXT},
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
