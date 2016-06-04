package dhbk.android.database.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import dhbk.android.database.R;
import dhbk.android.database.fragment.LoginFragment;
import dhbk.android.database.fragment.RegisterFragment;
import dhbk.android.database.fragment.ShowPostFragment;
import dhbk.android.database.models.User;
import dhbk.android.database.utils.DatabaseUserHelper;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, RegisterFragment.OnFragmentInteractionListener{
    private static final String TAG_LOGIN_FRAGMENT = "login_fragment";
    private static final String TAG_SHOW_POST_FRAGMENT = "show_post_fragment";
    private static final String TAG_REGISTER_FRAGMENT = "register_fragment";

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
    public void onReplaceShowPostFragmentInteraction(@NonNull String emailText) {
        // TODO: 6/4/16 check infor in edt is the same as database
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_LOGIN_FRAGMENT);
        if (fragment instanceof LoginFragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_container, ShowPostFragment.newInstance(emailText), TAG_SHOW_POST_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
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
    public void onRegisterFragmentInteractionAutoComplete(@NonNull String name,@NonNull String email,@NonNull String pass) {
        Fragment fragmentRegister = getSupportFragmentManager().findFragmentByTag(TAG_REGISTER_FRAGMENT);
        if (fragmentRegister instanceof RegisterFragment) {
            getSupportFragmentManager().popBackStack();
        }

        Fragment fragmentLogin = getSupportFragmentManager().findFragmentByTag(TAG_LOGIN_FRAGMENT);
        if (fragmentLogin instanceof LoginFragment) {
            ((LoginFragment)fragmentLogin).setEmail(email);
            ((LoginFragment)fragmentLogin).setPass(pass);
        }
    }

    // add data in registerfragment to database
    @Override
    public void onRegisterFragmentInteractionAddDatabase(@NonNull String name,@NonNull String email,@NonNull String pass) {
        User user = new User(name, email, pass);
        DatabaseUserHelper databaseUserHelper = DatabaseUserHelper.getInstance(this.getApplicationContext());
        databaseUserHelper.addUserToDatabase(user);
    }
}
