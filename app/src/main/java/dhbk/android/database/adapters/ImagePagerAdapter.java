package dhbk.android.database.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import dhbk.android.database.fragment.ImageDetailFragment;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    private final int mSize;
    private int mPosition;

    public ImagePagerAdapter(FragmentManager fm, int size) {
        super(fm);
        mSize = size;
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public Fragment getItem(int position) {
        mPosition = position;
        return ImageDetailFragment.newInstance(position);
    }

    // de lay' vi tri hien tai cua tam anh, tu` do ta co duoc image cua tam anh trong array
    public int getPosition() {
        return mPosition;
    }
}