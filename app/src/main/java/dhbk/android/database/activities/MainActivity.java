package dhbk.android.database.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import dhbk.android.database.R;
import dhbk.android.database.fragment.LoginFragment;
import dhbk.android.database.fragment.ShowPostFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener{
    private static final String TAG_LOGIN_FRAGMENT = "login_fragment";
    private static final String TAG_SHOW_POST_FRAGMENT = "show_post_fragment";

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
    public void onReplaceFragmentInteraction(@NonNull String emailText) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_LOGIN_FRAGMENT);
        if (fragment instanceof LoginFragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_container, ShowPostFragment.newInstance(emailText), TAG_SHOW_POST_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
