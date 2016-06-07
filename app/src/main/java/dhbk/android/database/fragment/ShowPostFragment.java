package dhbk.android.database.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import dhbk.android.database.R;
import dhbk.android.database.adapters.ShowPostRecyclerAdapter;

public class ShowPostFragment extends Fragment {
    private static final String ARG_NAME = "name";
    private static final String ARG_EMAIL = "email";
    private static final String ARG_IMG = "image";
    private static final String ARG_HAS_POST = "hasPost";

    private String mName;
    private String mEmail;
    private int mImg;
    private boolean mFirst = true;

    private OnFragmentInteractionListener mListener;
    private int mHasPost; // giá trị 0(chưa có table posts của người này trong database) hay 1(đã có table post của người này trong database)
    private RecyclerView mPostRecyclerView;
    private ShowPostRecyclerAdapter mShowPostRecyclerAdapter;

    public ShowPostFragment() {
        // Required empty public constructor
    }


    public static ShowPostFragment newInstance(String name, String email, int img, int hasPost) {
        ShowPostFragment fragment = new ShowPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_EMAIL, email);
        args.putInt(ARG_IMG, img);
        args.putInt(ARG_HAS_POST, hasPost);
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(ARG_NAME);
            mEmail = getArguments().getString(ARG_EMAIL);
            mImg = getArguments().getInt(ARG_IMG);
            mHasPost = getArguments().getInt(ARG_HAS_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_post, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        setHasOptionsMenu(true);
        toolbar.setTitle(mName);

        mPostRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview_show_posts);
        showPostFromUser();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_show_post, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_post) {
            // : 6/5/16 add image from galery to database
            if (mListener != null) {
                mListener.onReplaceAddPostFrag(mEmail);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void showPostFromUser() {
        if (mHasPost == 0) {
            // create table
            if (mListener != null) {
                mListener.onCreateUserPostTable(mEmail);
            }

        } else {
            // query table to show post in recycler
            // : 6/5/16 query showpost table
            refreshRecyclerView();
        }
    }

    // refresh recyler view, query to database and wait
    public void refreshRecyclerView() {
        // query databse
        if (mListener != null) {
            mListener.onQueryPostDatabase(mEmail);
        }
    }

    // after database return the result, update recyler
    public void updateRecyclerView(Cursor resultCursor) {
        // first created adapter, after second, change cursor
        if (mFirst) {
            mShowPostRecyclerAdapter = new ShowPostRecyclerAdapter(getActivity().getApplicationContext(), resultCursor);
            mPostRecyclerView.setAdapter(mShowPostRecyclerAdapter);
            mPostRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            mFirst = false;
        } else {
//            mShowPostRecyclerAdapter.changeCursor(resultCursor);
//            mShowPostRecyclerAdapter.notifyDataSetChanged();
            mShowPostRecyclerAdapter = new ShowPostRecyclerAdapter(getActivity().getApplicationContext(), resultCursor);
            mPostRecyclerView.setAdapter(mShowPostRecyclerAdapter);
        }
    }


    public interface OnFragmentInteractionListener {
        void onCreateUserPostTable(String email); // tạo table tên là email
        void onReplaceAddPostFrag(String email); // replace this fragment with addpost fragment, email is the name of user post's table
        void onQueryPostDatabase(String email);// query table email
    }
}
