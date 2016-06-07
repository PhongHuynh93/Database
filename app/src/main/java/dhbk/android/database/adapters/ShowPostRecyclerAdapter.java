package dhbk.android.database.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dhbk.android.database.R;
import dhbk.android.database.models.Post;

/**
 * Created by phongdth.ky on 6/7/2016.
 */
public class ShowPostRecyclerAdapter extends
        CursorRecyclerViewAdapter<ShowPostRecyclerAdapter.ViewHolder> {

    public ShowPostRecyclerAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Post myListItem = Post.fromCursor(cursor);
        viewHolder.postImgView.setImageResource(myListItem.getMesId());
        viewHolder.titleTextView.setText(myListItem.getTextTitle());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_post, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView titleTextView;
        public ImageView postImgView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            postImgView = (ImageView) itemView.findViewById(R.id.post_image);
            titleTextView = (TextView) itemView.findViewById(R.id.post_title);
        }
    }
}
