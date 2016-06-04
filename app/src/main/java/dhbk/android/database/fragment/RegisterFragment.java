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

import dhbk.android.database.R;

public class RegisterFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpView();
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // TODO: 6/4/16 check error when register etc empty field
    // giả sử đã điền đầy đủ 4 thông tin, cất name, email, pass word vào database
    private void setUpView() {
        Button registerButton = (Button) getActivity().findViewById(R.id.btn_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameEdt = (EditText) getActivity().findViewById(R.id.edt_name);
                String nameText = nameEdt.getText().toString();
                EditText emailEdt = (EditText) getActivity().findViewById(R.id.edt_email);
                String emailText = emailEdt.getText().toString();
                EditText passEdt = (EditText) getActivity().findViewById(R.id.edt_password);
                String passText = passEdt.getText().toString();

                // TODO: 6/4/16 check duplicate email by entering database and compare
                if (mListener != null) {
                    mListener.onRegisterFragmentInteractionAddDatabase(nameText, emailText, passText);
                    mListener.onRegisterFragmentInteractionAutoComplete(nameText, emailText, passText);
                }
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // when complete register, call this to complete email and pass in login layout
        void onRegisterFragmentInteractionAutoComplete(String name,String email,String pass);
        void onRegisterFragmentInteractionAddDatabase(String name, String email, String pass);
    }
}
