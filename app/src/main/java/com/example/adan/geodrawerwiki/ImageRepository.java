package com.example.adan.geodrawerwiki;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageRepository {
	
	private Context mContext;
	
	public ImageRepository(Context context)
	{
		mContext = context;
	}

	private File getStoragePath()
	{
	    File storageDir = null;
	    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	    	storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/geoPhoto/");
	    else
	    	storageDir = new File(mContext.getFilesDir().getAbsolutePath() + "/geoPhoto/");

	    return storageDir;
	}
	
	public File createImageFile()
	{
		//TODO
		// Create an image file name
	    try {
		    SimpleDateFormat timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
		    String imageFileName = timeStamp.format(new Date(0));
	
		    File storageDir = getStoragePath();
		    storageDir.mkdirs();
		    
			File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
			
			return image;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

	    // Save a file: path for use with ACTION_VIEW intents
	    return null;
	}
	
	public File[] getPhotoList()
	{
	    File storageDir = getStoragePath();
	    
	    try {
	    	
	    	return storageDir.listFiles();
	    				
		} catch (Exception e) {
			// TODO: handle exception
		}
	    
	    return null;
	}

    static public File[] getFolderContent(String folder)
    {
        File storageDir = new File(folder);
        try {

            return storageDir.listFiles();

        } catch (Exception e) {
            // TODO: handle exception
        }

        return null;
    }
}
