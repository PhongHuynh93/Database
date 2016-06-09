
public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
// trong gist recyclerview
// 


###########################################################################
// recyclerview + cursoradapter + onlick item
public class ShowPostRecyclerAdapter extends
        CursorRecyclerViewAdapter<ShowPostRecyclerAdapter.ViewHolder> {

    private static final String TAG = ShowPostRecyclerAdapter.class.getSimpleName();
    private OnFragmentInteractionListener mListener;

    public ShowPostRecyclerAdapter(Context context, Cursor cursor){
        super(context,cursor);
        if (context instanceof MainActivity) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, final int position) {
        final Post myListItem = Post.fromCursor(cursor);
        viewHolder.postImgView.setImageResource(myListItem.getMesId());
        viewHolder.titleTextView.setText(myListItem.getTextTitle());
        viewHolder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: click nút SHARE ở position " + position);
            }
        });
        viewHolder.readMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: click nút READ MORE ở position " + position);
                mListener.onChangeDetailPostFragment(myListItem.getTextTitle(), myListItem.getTextBody(), myListItem.getMesId());
            }
        });

        viewHolder.editImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onBindViewHolder: position " + position);
                String table_name = mListener.onGetTableName();
                mListener.onChangeEditPostFragment(table_name, myListItem.getTextTitle(), myListItem.getTextBody(), myListItem.getMesId(), position);
            }
        });

        // TODO: 6/8/2016 check delete button
        viewHolder.removeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        public ImageView editImgView;
        public ImageView removeImgView;
        public TextView shareBtn;
        public TextView readMoreBtn;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            postImgView = (ImageView) itemView.findViewById(R.id.post_image);
            titleTextView = (TextView) itemView.findViewById(R.id.post_title);
            shareBtn = (TextView) itemView.findViewById(R.id.tv_share);
            readMoreBtn = (TextView) itemView.findViewById(R.id.tv_read_more);
            editImgView = (ImageView) itemView.findViewById(R.id.button_edit);
            removeImgView = (ImageView) itemView.findViewById(R.id.button_delete);
        }
    }

    public interface OnFragmentInteractionListener {
        // pass title, body và resId and change to detail fragment
        void onChangeDetailPostFragment(String title, String body, int resId);

        // change to edit fragment
        void onChangeEditPostFragment(String getNamePostable, String getTextTitle, String getTextBody, int getMesId, int position);

        String onGetTableName();
    }
}


###########################################################################
// post model
public class Post {
    private String namePostable;
    public int mesId;
    public String textTitle;
    public String textBody;

    public Post(String namePostable, int mesId, String textTitle, String textBody) {
        this.namePostable = namePostable;
        this.mesId = mesId;
        this.textTitle = textTitle;
        this.textBody = textBody;
    }

    public Post(String namePostable, String textTitle, String textBody) {
        this.namePostable = namePostable;
        this.textTitle = textTitle;
        this.textBody = textBody;
    }

    public Post(int mesId, String textTitle, String textBody) {
        this.mesId = mesId;
        this.textTitle = textTitle;
        this.textBody = textBody;
    }



    public int getMesId() {
        return mesId;
    }

    public String getTextTitle() {
        return textTitle;
    }

    public String getTextBody() {
        return textBody;
    }

    public String getNamePostable() {
        return namePostable;
    }

    @Nullable
    public static Post fromCursor(Cursor cursor) {
        if(cursor!=null) {
            int resImg = cursor.getInt(cursor.getColumnIndex(DatabaseUserHelper.KEY_POST_IMAGE));
            String titleMes = cursor.getString(cursor.getColumnIndex(DatabaseUserHelper.KEY_POST_TITLE_TEXT));
            String bodyMes = cursor.getString(cursor.getColumnIndex(DatabaseUserHelper.KEY_POST_BODY_TEXT));
            return new Post(resImg, titleMes, bodyMes);
        }
        return null;
    }
}