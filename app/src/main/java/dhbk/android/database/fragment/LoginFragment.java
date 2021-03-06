package dhbk.android.database.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import dhbk.android.database.R;
import dhbk.android.database.models.User;
import dhbk.android.database.utils.BitmapWorkerTask;
import dhbk.android.database.utils.Constant;
import dhbk.android.database.utils.DatabaseUserHelper;

public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String mEmail;
    private String mPass;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scaleBackgroundImage();
        setUpView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // set value at another time in order to not type again
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String pref_name = sharedPref.getString(Constant.PREF_NAME, "");
        String pref_pass = sharedPref.getString(Constant.PREF_PASS, "");
        EditText edtName = (EditText) getActivity().findViewById(R.id.edt_email);
        edtName.setText(pref_name);
        EditText edtPass = (EditText) getActivity().findViewById(R.id.edt_pass);
        edtPass.setText(pref_pass);

        // after register account, 2 var will have value, so set this login fragment
        if (mEmail != null && mPass != null) {
            edtName = (EditText) getActivity().findViewById(R.id.edt_email);
            edtName.setText(mEmail);
            edtPass = (EditText) getActivity().findViewById(R.id.edt_pass);
            edtPass.setText(mPass);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void setPass(String pass) {
        mPass = pass;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPass() {
        return mPass;
    }

    // check email and pass in database
    @Nullable
    public User checkUserAccount(String emailText, String passText) {
        DatabaseUserHelper db = DatabaseUserHelper.getInstance(getActivity().getApplicationContext());
        User userAccount = db.getUserFromDatabase(emailText);
        if (userAccount != null && emailText.equals(userAccount.getUserEmail()) && passText.equals(userAccount.getUserPass())) {
            return userAccount;
        } else {
            return null;
        }

    }

    private void scaleBackgroundImage() {
        ImageView backgroundImageView = (ImageView) getActivity().findViewById(R.id.image_login_bg);
        BitmapWorkerTask task = new BitmapWorkerTask(backgroundImageView, getActivity().getApplicationContext(), 500, 500);
        task.execute(R.drawable.bg_apple);
    }

    private void setUpView() {
        Button loginButton = (Button) getActivity().findViewById(R.id.btn_log_in);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    EditText emailEdt = (EditText) getActivity().findViewById(R.id.edt_email);
                    setEmail(emailEdt.getText().toString());

                    EditText passEdt = (EditText) getActivity().findViewById(R.id.edt_pass);
                    setPass(passEdt.getText().toString());
                    mListener.onReplaceShowPostFragmentInteraction(getEmail(), getPass());
                }
            }
        });

        Button registerButton = (Button) getActivity().findViewById(R.id.btn_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onReplaceRegisterFragmentInteraction();
                }
            }
        });

    }

    public interface OnFragmentInteractionListener {
        void onReplaceShowPostFragmentInteraction(String name, String passText);
        void onReplaceRegisterFragmentInteraction();
    }
}
