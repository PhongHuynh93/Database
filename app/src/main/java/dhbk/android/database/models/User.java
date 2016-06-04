package dhbk.android.database.models;

/**
 * Created by huynhducthanhphong on 6/4/16.
 */
public class User {
    private int userImg;
    public String userName;
    public String userPass;
    public String userEmail;

    public User(String userName, String userEmail, String userPass) {
        this.userName = userName;
        this.userPass = userPass;
        this.userEmail = userEmail;
    }

    public User(String userName, String userEmail, String userPass, int userImg) {
        this.userName = userName;
        this.userPass = userPass;
        this.userEmail = userEmail;
        this.userImg = userImg;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getUserImg() {
        return userImg;
    }
}
