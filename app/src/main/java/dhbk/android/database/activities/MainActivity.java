package dhbk.android.database.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dhbk.android.database.R;
import dhbk.android.database.fragment.AddPostFragment;
import dhbk.android.database.fragment.LoginFragment;
import dhbk.android.database.fragment.RegisterFragment;
import dhbk.android.database.fragment.ShowPostFragment;
import dhbk.android.database.models.User;
import dhbk.android.database.utils.Constant;
import dhbk.android.database.utils.DatabaseUserHelper;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, RegisterFragment.OnFragmentInteractionListener, ShowPostFragment.OnFragmentInteractionListener, AddPostFragment.OnFragmentInteractionListener,
        DatabaseUserHelper.OnDatabaseInteractionListener {
    private static final String TAG_LOGIN_FRAGMENT = "login_fragment";
    private static final String TAG_SHOW_POST_FRAGMENT = "show_post_fragment";
    private static final String TAG_REGISTER_FRAGMENT = "register_fragment";
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 100;
    private static final String TAG_ADD_POST_FRAGMENT = "add_post_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addLoginFragment();
    }

    private void addLoginFragment() {
        FragmentManager fm = getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fm.findFragmentByTag(TAG_LOGIN_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance();
            fm.beginTransaction().add(R.id.root_container, loginFragment, TAG_LOGIN_FRAGMENT).commit();
        }

    }

    // replace LoginFragment with ShowPostFragment
    @Override
    public void onReplaceShowPostFragmentInteraction(@NonNull String emailText, @NonNull String passText) {
        // TODO: 6/4/16 check infor in edt is the same as database
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_LOGIN_FRAGMENT);
        if (fragment instanceof LoginFragment) {
            User userAccount = ((LoginFragment) fragment).checkUserAccount(emailText, passText);

            if (userAccount != null) {
                // authen success -> save to sharepreference to not type again
                SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(Constant.PREF_NAME, userAccount.getUserEmail());
                editor.putString(Constant.PREF_PASS, userAccount.getUserPass());
                editor.apply();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.root_container, ShowPostFragment.newInstance(userAccount.getUserName(), userAccount.getUserEmail(), userAccount.getUserImg(), userAccount.getHasPost()), TAG_SHOW_POST_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(MainActivity.this, "Please check email or password again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // replace LoginFragment with RegisterFragment
    @Override
    public void onReplaceRegisterFragmentInteraction() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_LOGIN_FRAGMENT);
        if (fragment instanceof LoginFragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_container, RegisterFragment.newInstance(), TAG_REGISTER_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // replace register fragment with login fragment, fragment login is in front of, and we can autocomplete with field in registerfragment
    @Override
    public void onRegisterFragmentInteractionAutoComplete(@NonNull String name, @NonNull String email, @NonNull String pass) {
        Fragment fragmentRegister = getSupportFragmentManager().findFragmentByTag(TAG_REGISTER_FRAGMENT);
        if (fragmentRegister instanceof RegisterFragment) {
            getSupportFragmentManager().popBackStack();
        }

        Fragment fragmentLogin = getSupportFragmentManager().findFragmentByTag(TAG_LOGIN_FRAGMENT);
        if (fragmentLogin instanceof LoginFragment) {
            ((LoginFragment) fragmentLogin).setEmail(email);
            ((LoginFragment) fragmentLogin).setPass(pass);
        }
    }

    // add data in registerfragment to database
    @Override
    public void onRegisterFragmentInteractionAddDatabase(@NonNull String name, @NonNull String email, @NonNull String pass) {
        User user = new User(name, email, pass);
        DatabaseUserHelper databaseUserHelper = DatabaseUserHelper.getInstance(this.getApplicationContext());
        databaseUserHelper.addUserToDatabase(user);
    }

    // post image to showpostfragment when click button add on toolbar
    @Override
    public void onAddImageToImgView() {
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        } else {
            getImageFromGaleryFolder();
        }
    }

    // pop addPostFragment out of backstack
    @Override
    public void onPopAddPostFragOut() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_ADD_POST_FRAGMENT);
        if (fragment instanceof AddPostFragment) {
            getSupportFragmentManager()
                    .popBackStack();
        }
    }

    // add a post from addPopFragment to database
    @Override
    public void onAddMessagePostToDB(String namePostTable, int imgId, String mesTitle, String mesBody) {
        DatabaseUserHelper db = DatabaseUserHelper.getInstance(getApplicationContext());
        db.makeMessageInPostTable(namePostTable, imgId, mesTitle, mesBody);
    }

    // refresh recycler by connect to database to query user post, show posts after user add a post
    @Override
    public void onRefreshRecylerView() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_SHOW_POST_FRAGMENT);
        if (fragment instanceof ShowPostFragment) {
            ((ShowPostFragment) fragment).refreshRecyclerView();
        }
    }

    // tao table user posts tên là email chỉ khi
    @Override
    public void onCreateUserPostTable(@NonNull String email) {
        DatabaseUserHelper db = DatabaseUserHelper.getInstance(getApplicationContext());
        db.createPostTable(email);
    }

    // replace ShowPostFragment with AddPostFragment
    @Override
    public void onReplaceAddPostFrag(String email) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_SHOW_POST_FRAGMENT);
        if (fragment instanceof ShowPostFragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_container, AddPostFragment.newInstance(email), TAG_ADD_POST_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // query table name email
    @Override
    public void onQueryPostDatabase(String email) {
        DatabaseUserHelper db = DatabaseUserHelper.getInstance(getApplicationContext());
        db.queryPostTable(this ,email);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        List<String> permissions = new ArrayList<>();
        String message = "Database permissions:";

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            message += "\nStorage access to get image from Galery.";
        }

        if (!permissions.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            String[] params = permissions.toArray(new String[permissions.size()]);
            requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        } else {
            getImageFromGaleryFolder();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                Boolean storage = perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if (storage) {
                    Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Storage permission is required to get image from galery", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // get image from galery by sending intent
    private void getImageFromGaleryFolder() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, Constant.RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Fragment fragmentAddPost = getSupportFragmentManager().findFragmentByTag(TAG_ADD_POST_FRAGMENT);
            if (fragmentAddPost instanceof AddPostFragment) {
                ((AddPostFragment) fragmentAddPost).addImageToImgViet(data);
            }
        }
    }

    // after database query table post, it's return a cursor
    // we use this cursor to pass to recyclerview
    @Override
    public void onResultPostTable(Cursor result) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_SHOW_POST_FRAGMENT);
        if (fragment instanceof ShowPostFragment) {
            ((ShowPostFragment)fragment).updateRecyclerView(result);
        }
    }
}
