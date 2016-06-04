package dhbk.android.database.models;

/**
 * Created by huynhducthanhphong on 6/4/16.
 */
public class User {
    public String userName;
    public String userPass;
    public String userEmail;
    public String profilePictureUrl;

    public User(String userName, String userPass, String userEmail) {
        this.userName = userName;
        this.userPass = userPass;
        this.userEmail = userEmail;
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
}
