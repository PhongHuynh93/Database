package dhbk.android.database.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import dhbk.android.database.R;

public class ShowPostFragment extends Fragment {
    private static final String ARG_NAME = "name";
    private static final String ARG_EMAIL = "email";
    private static final String ARG_IMG = "image";
    private static final int IMAGE_HEIGHT = 500; // height + width of scaled image
    private static final int IMAGE_WIDTH = 500;
    private static final String ARG_HAS_POST = "hasPost";

    private String mName;
    private String mEmail;
    private int mImg;

    private OnFragmentInteractionListener mListener;
    private int mHasPost; // giá trị 0(chưa có table posts của người này trong database) hay 1(đã có table post của người này trong database)

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
                mListener.onReplaceAddPostFrag();
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

    // add image to database and refresh recycler view
    public void addImageToDbAndRefreshRcv(Intent data) {
        // FIXME: 6/5/16 add post to this
//        Uri selectedImage = data.getData();
//        String[] filePathColumn = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
//        if(cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()) {
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//
////            RecyclerView backgroundRclv = (RecyclerView) getActivity().findViewById(R.id.recyclerview_show_posts);
////            BitmapWorkerFromFileTask bitmapWorkerFromFileTask = new BitmapWorkerFromFileTask(backgroundRclv, picturePath);
////            bitmapWorkerFromFileTask.execute(IMAGE_HEIGHT, IMAGE_WIDTH);
//
//            cursor.close();
//        }
    }


    private void showPostFromUser() {
        if (mHasPost == 0) {
            // create table
            if (mListener != null) {
                mListener.onCreateUserPostTable(mEmail);
            }

        } else {
            // query table to show post in recycler
            // TODO: 6/5/16 query showpost table

        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void onPostImageToRecyclerView();

        void onCreateUserPostTable(String email); // tạo table tên là email

        void onReplaceAddPostFrag(); // replace this fragment with addpost fragment
    }
}
