package dhbk.android.database.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import dhbk.android.database.R;
import dhbk.android.database.adapters.ImagePagerAdapter;
import dhbk.android.database.utils.BitmapWorkerFromFileTask;

public class AddPostFragment extends Fragment {
    private static final String ARG_EMAIL = "email_name_table"; // là tên table chứa post của 1 user có email
    private OnFragmentInteractionListener mListener;
    // dimen cua image ma ta scale
    private static final int IMAGE_HEIGHT = 500;
    private static final int IMAGE_WIDTH = 500;
    // image to choose in order to post
    public static final int[] IMAGE_PAGE_VIEWER =
            {
                    R.drawable.one,
                    R.drawable.two,
                    R.drawable.three,
                    R.drawable.four,
                    R.drawable.five,
                    R.drawable.six,
                    R.drawable.seven,
                    R.drawable.eight,
                    R.drawable.nine,
                    R.drawable.add_post_image

            };
    private String mNamePostTable;

    public AddPostFragment() {
    }

    public static AddPostFragment newInstance(String email) {
        AddPostFragment fragment = new AddPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNamePostTable = getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        setHasOptionsMenu(true);
        toolbar.setTitle(getActivity().getResources().getString(R.string.title_add_post));

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

    // this method is called after we choose an image from galery, we change image from viewpager
    public void addImageToImgViet(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            // lầy imageview từ viewpager
            ViewPager chooseImgViewPager = (ViewPager) getActivity().findViewById(R.id.viewpager_add_image_post);
            ImageView img = (ImageView) chooseImgViewPager.findViewById(R.id.imgView_view_pager);

            // nén hình lại và set lên imageview
            BitmapWorkerFromFileTask bitmapWorkerFromFileTask = new BitmapWorkerFromFileTask(img, picturePath);
            bitmapWorkerFromFileTask.execute(IMAGE_HEIGHT, IMAGE_WIDTH);

            cursor.close();
        }
    }

    private void setupView() {
        ViewPager chooseImgViewPager = (ViewPager) getActivity().findViewById(R.id.viewpager_add_image_post);
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(getChildFragmentManager(), IMAGE_PAGE_VIEWER.length);
        chooseImgViewPager.setAdapter(imagePagerAdapter);

        Button backButton = (Button) getActivity().findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPopAddPostFragOut();
                }
            }
        });
        Button addButton = (Button) getActivity().findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add 3 field in database
                ViewPager chooseImgViewPager = (ViewPager) getActivity().findViewById(R.id.viewpager_add_image_post);
                ImageView img = (ImageView) chooseImgViewPager.findViewById(R.id.imgView_view_pager);

                EditText mesTitleEdt = (EditText) getActivity().findViewById(R.id.message_title);
                EditText mesBodyEdt = (EditText) getActivity().findViewById(R.id.message_body);

                Integer resId = (Integer) img.getTag(); // lấy file id của image đang hiện và lưu vào database
                String mesTitle = mesTitleEdt.getText().toString();
                String mesBody = mesBodyEdt.getText().toString();

                if (mListener != null) {
                    mListener.onAddMessagePostToDB(mNamePostTable, resId, mesTitle, mesBody);
                    // come back to showpostfragment
                    mListener.onPopAddPostFragOut();
                    // refresh recycler view
                    mListener.onRefreshRecylerView();
                }


            }
        });
    }

    // lấy hình từ galery
    public void getImageFromGalery() {
        if (mListener != null) {
            mListener.onAddImageToImgView();
        }
    }

    public interface OnFragmentInteractionListener {
        void onAddImageToImgView(); // go to galery, get image and add iamge

        void onPopAddPostFragOut(); // pop addPost Fragment out

        void onAddMessagePostToDB(String namePostTable, int imgId, String mesTitle, String mesBody); // add post to database

        void onRefreshRecylerView(); // refresh lại list các bài post sau khi add
    }
}
