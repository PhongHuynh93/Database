package dhbk.android.database.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import dhbk.android.database.R;

public class EditPostFragment extends Fragment {
    private static final String ARG_EMAIL = "email_name_table"; // là tên table chứa post của 1 user có email
    private static final String ARG_NAME_POST_TABLE = "posttable";
    private static final String ARG_NAME_TITLE = "title";
    private static final String ARG_NAME_BODY = "body";
    private static final String ARG_RES_IMAGE = "image";
    private static final String ARG_POSITION = "position";

    private OnFragmentInteractionListener mListener;
    // dimen cua image ma ta scale
    private static final int IMAGE_HEIGHT = 500;
    private static final int IMAGE_WIDTH = 500;
    private String mNamePostTable;
    private String mTitle;
    private String mBody;
    private int mResImg;
    private int mPosition;

    public EditPostFragment() {
    }

    public static EditPostFragment newInstance(@NonNull String namePostTable,@NonNull String nameTitle,@NonNull String nameBody, int resId, int position) {
        EditPostFragment fragment = new EditPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME_POST_TABLE, namePostTable);
        args.putString(ARG_NAME_TITLE, nameTitle);
        args.putString(ARG_NAME_BODY, nameBody);
        args.putInt(ARG_RES_IMAGE, resId);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNamePostTable = getArguments().getString(ARG_NAME_POST_TABLE);
            mTitle = getArguments().getString(ARG_NAME_TITLE);
            mBody = getArguments().getString(ARG_NAME_BODY);
            mResImg = getArguments().getInt(ARG_RES_IMAGE);
            mPosition = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_post, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        setHasOptionsMenu(true);
        toolbar.setTitle("Edit Post");

        setupView();
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

    private void setupView() {
        ImageView img = (ImageView) getActivity().findViewById(R.id.imgButton_editImg);
        img.setImageResource(mResImg);

        EditText titleEdt = (EditText) getActivity().findViewById(R.id.message_title);
        titleEdt.setText(mTitle);

        EditText bodyEdt = (EditText) getActivity().findViewById(R.id.message_body);
        bodyEdt.setText(mBody);

        Button backButton = (Button) getActivity().findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPopEditPostFragOut();
                }
            }
        });

        Button addButton = (Button) getActivity().findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText mesTitleEdt = (EditText) getActivity().findViewById(R.id.message_title);
                EditText mesBodyEdt = (EditText) getActivity().findViewById(R.id.message_body);

                String mesTitle = mesTitleEdt.getText().toString();
                String mesBody = mesBodyEdt.getText().toString();

                if (mListener != null) {
                    mListener.onUpdateMessagePostToDB(mNamePostTable, mesTitle, mesBody, mPosition);
                    // come back to showpostfragment
                    mListener.onPopEditPostFragOut();
                    // refresh recycler view
                    mListener.onRefreshRecylerView();
                }


            }
        });
    }

    // interface to talk to MainActivity
    public interface OnFragmentInteractionListener {
        void onPopEditPostFragOut(); // pop addPost Fragment out

        // update database
        void onUpdateMessagePostToDB(String namePostTable, String mesTitle, String mesBody, int position);

        void onRefreshRecylerView();
    }


}