package dhbk.android.database.models;

import android.database.Cursor;
import android.support.annotation.Nullable;

import dhbk.android.database.utils.DatabaseUserHelper;

/**
 * Created by huynhducthanhphong on 6/4/16.
 */
public class Post {
    private String namePostable;
    public int mesId;
    public String textTitle;
    public String textBody;

    public Post(String namePostable, int mesId, String textTitle, String textBody) {
        this.namePostable = namePostable;
        this.mesId = mesId;
        this.textTitle = textTitle;
        this.textBody = textBody;
    }

    public Post(String namePostable, String textTitle, String textBody) {
        this.namePostable = namePostable;
        this.textTitle = textTitle;
        this.textBody = textBody;
    }

    public Post(int mesId, String textTitle, String textBody) {
        this.mesId = mesId;
        this.textTitle = textTitle;
        this.textBody = textBody;
    }



    public int getMesId() {
        return mesId;
    }

    public String getTextTitle() {
        return textTitle;
    }

    public String getTextBody() {
        return textBody;
    }

    public String getNamePostable() {
        return namePostable;
    }

    @Nullable
    public static Post fromCursor(Cursor cursor) {
        if(cursor!=null) {
            int resImg = cursor.getInt(cursor.getColumnIndex(DatabaseUserHelper.KEY_POST_IMAGE));
            String titleMes = cursor.getString(cursor.getColumnIndex(DatabaseUserHelper.KEY_POST_TITLE_TEXT));
            String bodyMes = cursor.getString(cursor.getColumnIndex(DatabaseUserHelper.KEY_POST_BODY_TEXT));
            return new Post(resImg, titleMes, bodyMes);
        }
        return null;
    }
}
