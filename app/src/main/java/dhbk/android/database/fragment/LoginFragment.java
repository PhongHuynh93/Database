package dhbk.android.database.fragment;

import android.content.Context;
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
import dhbk.android.database.utils.BitmapWorkerTask;

public class LoginFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void scaleBackgroundImage() {
        ImageView backgroundImageView = (ImageView) getActivity().findViewById(R.id.image_login_bg);
        BitmapWorkerTask task = new BitmapWorkerTask(backgroundImageView, getActivity().getApplicationContext(), 500, 500);
        task.execute(R.drawable.bg_apple);
    }

    private void setUpView() {
        Button registerButton = (Button) getActivity().findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 6/4/16 check password and email by access database before alllow to enter to login.
                if (mListener != null) {
                    EditText emailEdt = (EditText) getActivity().findViewById(R.id.edt_email);
                    String emailText = emailEdt.getText().toString();
                    mListener.onReplaceFragmentInteraction(emailText);
                }
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onReplaceFragmentInteraction(String name);
    }
}
