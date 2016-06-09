###########################################################################
how to get image from galery
// get image from galery by sending intent
    private void getImageFromGaleryFolder() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, Constant.RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityRegsult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Fragment fragmentAddPost = getSupportFragmentManager().findFragmentByTag(TAG_ADD_POST_FRAGMENT);
            if (fragmentAddPost instanceof AddPostFragment) {
                ((AddPostFragment) fragmentAddPost).addImageToImgViet(data);
            }
        }
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

###########################################################################
how to get position of current ViewPager
int imageid =  AddPostFragment.IMAGE_PAGE_VIEWER[chooseImgViewPager.getCurrentItem()];

###########################################################################
scale bitmap from drawable with asynctask

  private void scaleBackgroundImage() {
        ImageView backgroundImageView = (ImageView) getActivity().findViewById(R.id.image_login_bg);
        // imageview, context, width, height
        BitmapWorkerTask task = new BitmapWorkerTask(backgroundImageView, getActivity().getApplicationContext(), 500, 500);
        task.execute(R.drawable.bg_apple);
    }

// scale drawable image (png, jpg)
public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private final Context mContext;
    private final int mReqWidth;
    private final int mReqHeight;
    private int data = 0;

    public BitmapWorkerTask(ImageView imageView, Context context, int reqWidth, int reqHeight) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        mContext = context.getApplicationContext();
        mReqWidth = reqWidth;
        mReqHeight = reqHeight;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        data = params[0];
        return decodeSampledBitmapFromResource(mContext.getResources(), data, mReqWidth, mReqHeight);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

###########################################################################
scale bitmap from file in galery with asynctask
// scale bitmap from file
public class BitmapWorkerFromFileTask extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private final String mPicturePath;

    public BitmapWorkerFromFileTask(ImageView imageView, String picturePath) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        mPicturePath = picturePath;
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        int reqWidth = params[0];
        int reqHeight = params[1];
        return decodeSampledBitmapFromResource(reqWidth, reqHeight);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap decodeSampledBitmapFromResource(int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPicturePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(mPicturePath, options);
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}


###########################################################################
fragment: how to refresh recyclerview in fragment transaction. We cannt changeCursor() in recyclerview and get old adapter, because in fragment transaction,
fragment''s onCreateView() is call again -> fragment make new recyclerview, so we must setAdapter() again.

	
  private void showPostFromUser() {
        if (mHasPost == 0) {
            // create table because no data in database
            if (mListener != null) {
                mListener.onCreateUserPostTable(mEmail);
            }

        // if have data in database, show it in recyclerview
        } else {
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

// after database return
// after database return the result, update recyler
    public void updateRecyclerView(Cursor resultCursor) {
        // chú ý la phải tạo lại recyclerview lý đo là viewroot cũ đã xóa cho nên recycler cũng bị xóa luôn.
        RecyclerView postRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview_show_posts);
        ShowPostRecyclerAdapter mShowPostRecyclerAdapter = new ShowPostRecyclerAdapter(getActivity(), resultCursor); // pass activity context
        postRecyclerView.setAdapter(mShowPostRecyclerAdapter);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        // space between row
        RecyclerView.ItemDecoration spaceDecoration = new
                SpacesItemDecoration(20);
        postRecyclerView.addItemDecoration(spaceDecoration);
    }
