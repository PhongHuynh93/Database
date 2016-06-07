package dhbk.android.database.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dhbk.android.database.R;

public class ImageDetailFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "resId";
    private static final String TAG = ImageDetailFragment.class.getName();
    private int mImageNum;
    private ImageView mImageView;

    public static ImageDetailFragment newInstance(int imageNum) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA, imageNum);
        f.setArguments(args);
        return f;
    }

    // Empty constructor, req uired as per Fragment docs
    public ImageDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageNum = getArguments() != null ? getArguments().getInt(IMAGE_DATA_EXTRA) : -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_detail, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imgView_view_pager);
        mImageView.setImageResource(AddPostFragment.IMAGE_PAGE_VIEWER[mImageNum]);
        // set tag là từng bức ảnh có tên giống y như resource để sau này lấy tên là lấy resource luôn
        mImageView.setTag(AddPostFragment.IMAGE_PAGE_VIEWER[mImageNum]);
        // nếu là image cuối thì khi click vào sẽ vô galery
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageNum == (AddPostFragment.IMAGE_PAGE_VIEWER.length - 1)) {
                    // call fragment parent to get image from galery
                    Fragment fragment = getParentFragment();
                    if (fragment instanceof AddPostFragment) {
                        ((AddPostFragment)fragment).getImageFromGalery();
                    }
                }
            }
        });
        return v;
    }
}
