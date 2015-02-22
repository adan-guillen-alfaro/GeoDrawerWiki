package com.example.adan.geodrawerwiki;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Adan on 24/1/15.
 */
public class galleryListAdapter extends BaseAdapter {
    
    private Context mContext = null;

    public class Album
    {
        public String name;
        public String id;
        public long coverID = 0;
        public int count = 0;
        public String folderName;

        public boolean hasGeoLocatedPhotos()
        {
            return true;
 /*
            try {
                File path = new File(folderName);

                File[] files = path.listFiles();
                if (files != null)
                {
                    for(File file : files)
                    {
                        ExifInterface exifData = new ExifInterface(file.getCanonicalPath());
                        geoDegree geo = new geoDegree(exifData);
                        if (geo.isValid())
                            return true;
                    }
                }

                return false;
            }
            catch (Exception e)
            {

            }

            return false;
 */       }
    };

    private final ArrayList<Album> mAlbumsList = new ArrayList<Album>();

    public galleryListAdapter(Context context)
    {
        mContext = context;
        getPhotoGalleries();

        if (mAlbumsList.size() == 0)
        {
            //TODO: Toast
        }
    }

    @Override
    public int getCount() {
        return mAlbumsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAlbumsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO: preview
        View view = null;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (View)inflater.inflate(R.layout.folder_preview, null);
            convertView = view;
        }
        else view = convertView;

        if (view != null)
        {
            Album album = (Album)getItem(position);

            TextView textView = (TextView)view.findViewById(R.id.folderName);
            textView.setText(album.name);

            final BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize =2;

            Bitmap thumbmail = MediaStore.Images.Thumbnails.getThumbnail(
                    mContext.getContentResolver(),
                    album.coverID,
                    MediaStore.Images.Thumbnails.MINI_KIND, bmOptions);

            if (thumbmail != null)
            {
                ImageView imageView = (ImageView)view.findViewById(R.id.imageView);

                double w = thumbmail.getWidth();
                double h = thumbmail.getHeight();

                double ratio = 1.0;
                if (w != 0 && h != 0)
                    ratio = w / h;

                imageView.setImageBitmap(Bitmap.createScaledBitmap(thumbmail, (int)(ratio * 200.0), 200, false));
            }

        }

        return view;
    }

    private void getPhotoGalleries()
    {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATA };

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

        ArrayList<String> ids = new ArrayList<String>();
        mAlbumsList.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Album album = new Album();

                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                album.id = cursor.getString(columnIndex);

                if (!ids.contains(album.id)) {
                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    album.name = cursor.getString(columnIndex);

                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    album.coverID = cursor.getLong(columnIndex);

                    columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    String folderName = cursor.getString(columnIndex);


                    File file = new File(folderName);

                    album.folderName = file.getParent();

                    if (album.hasGeoLocatedPhotos()) {
                        mAlbumsList.add(album);
                        ids.add(album.id);
                    }
                } else {
                    mAlbumsList.get(ids.indexOf(album.id)).count++;
                }
            }
            cursor.close();
        }
    }
}
