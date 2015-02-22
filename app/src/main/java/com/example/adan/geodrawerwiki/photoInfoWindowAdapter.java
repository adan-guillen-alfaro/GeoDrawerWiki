package com.example.adan.geodrawerwiki;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by Adan on 18/1/15.
 */
class photoInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    MapsActivity mParentActivity = null;

    public final ArrayList<Marker> mMapInfoMarkersValues = new ArrayList<Marker>();
    public final ArrayList<Marker> mMapPhotoMarkersValues = new ArrayList<Marker>();
    private final ArrayList<Bitmap> mMapThumbnailsValues = new ArrayList<Bitmap>()
            ;
    public enum markerType
    {
        PHOTO   ,
        INFO    ,
    };

    photoInfoWindowAdapter(MapsActivity activity)
    {
        mParentActivity = activity;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

/*
        if (getMarkerType(marker) == markerType.PHOTO)
        {
            View view = mParentActivity.getLayoutInflater().inflate(R.layout.photo_marker, null);

            ImageView preview = (ImageView)view.findViewById(R.id.imageView);
            if (preview != null)
            {
                Bitmap bitmap = getThumbnailFor(marker);
                if (bitmap != null)
                    preview.setImageBitmap(bitmap);
            }
            return view;
        }
*/
        return null;
    }

    markerType getMarkerType(Marker marker)
    {
        if (mMapPhotoMarkersValues.contains(marker))
            return markerType.PHOTO;
        else
            return markerType.INFO;
    }

    public void removeMapInfoMarkers()
    {
        for (Marker mark : mMapInfoMarkersValues)
            mark.remove();

        mMapInfoMarkersValues.clear();
    }

    public void addMapInfoMarker(Marker marker)
    {
        mMapInfoMarkersValues.add(marker);
    }

    public void removeMapPhotoMarkers()
    {
        for (Marker marker : mMapPhotoMarkersValues)
            marker.remove();

        mMapPhotoMarkersValues.clear();
        mMapThumbnailsValues.clear();
    }

    public void addMapPhotoMarker(Marker marker, Bitmap bitmap)
    {
        mMapPhotoMarkersValues.add(marker);
        mMapThumbnailsValues.add(bitmap);
    }

    public Bitmap getThumbnailFor(Marker marker)
    {
        int id = mMapPhotoMarkersValues.indexOf(marker);
        if (id > -1 && id < mMapThumbnailsValues.size())
            return mMapThumbnailsValues.get(id);

        return null;
    }
}
