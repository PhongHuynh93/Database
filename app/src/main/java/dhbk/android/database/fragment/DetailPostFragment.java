package dhbk.android.database.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dhbk.android.database.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailPostFragment extends Fragment {
    private static final String ARG_TEXT_TITLE = "title";
    private static final String ARG_TEXT_BODY = "body";
    private static final String ARG_IMAGE_ID = "image";

    private OnFragmentInteractionListener mListener;
    private String mTextTitle;
    private String mTextBody;
    private int mImageId;

    public DetailPostFragment() {
        // Required empty public constructor
    }

    public static DetailPostFragment newInstance(String title, String body, int resId) {
        DetailPostFragment fragment = new DetailPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT_TITLE, title);
        args.putString(ARG_TEXT_BODY, body);
        args.putInt(ARG_IMAGE_ID, resId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTextTitle = getArguments().getString(ARG_TEXT_TITLE);
            mTextBody = getArguments().getString(ARG_TEXT_BODY);
            mImageId = getArguments().getInt(ARG_IMAGE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_detail_post, container, false);
        TextView titleTv = (TextView) v.findViewById(R.id.tv_title_detail_post);
        TextView bodyTv = (TextView) v.findViewById(R.id.tv_body_detail_post);
        ImageView img = (ImageView) v.findViewById(R.id.image_detail_post);

        titleTv.setText(mTextTitle);
        bodyTv.setText(mTextBody);
        img.setImageResource(mImageId);

        return v;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
