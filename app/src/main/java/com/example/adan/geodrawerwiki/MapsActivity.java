package com.example.adan.geodrawerwiki;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private photoInfoWindowAdapter mPhotoInfoWindowAdapter = null;
    private geoLocationManager mLocationManager = null;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private Location mCurrentLocation = null;
    private final ArrayList<geoLocation> mGeoLocationvalues = new ArrayList<geoLocation>();


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GALLERY_IMAGE = 2;
    static final int REQUEST_GALLERY = 3;

 //   private static final long ALARM_DELAY = 2* 60 * 1000;

    Uri mLastPicture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.attachParent(this);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mLocationManager = new geoLocationManager(this);
        mCurrentLocation = mLocationManager.getLocation();

        setUpMapIfNeeded();
    }


    @Override
    protected void onResume() {
        super.onResume();

        mLocationManager.onResume();
        mCurrentLocation = mLocationManager.getLocation();

        if (mCurrentLocation != null)
        {
            searchForData();
        }

        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mLocationManager.onPause();
    }

    public void onLocationChanged(Location location)
    {
        if (location == null)
            return;

        if (mCurrentLocation == null)
            mCurrentLocation = location;
        else
            if (mCurrentLocation.distanceTo(location) < geoLocationManager.MIN_DISTANCE)
                return;

        mCurrentLocation = location;
        searchForData();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        try
        {
            geoLocation location = mGeoLocationvalues.get(position);
            CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            mMap.animateCamera(center, 1000, null);

/*
            for (int i = 0 ; i < mMapMarkersValues.size() ; i++)
            {
                MarkerOptions marker = mMapMarkersValues.get(i);
                geoLocation loc = findLocationFor()
                if (loc != null)
                {
                    if (loc.describeContents() == location.describeContents())
                    {

                    }
                }

            }
*/

        }
        catch (Exception e)
        {

        }
        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
/*
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
*/
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_photo) {
            if (takePhoto())
                return true;
        }
        else if (id == R.id.action_read_photo)
        {
            if (getGalleryPhoto())
                return true;
        }
        else if (id == R.id.action_read_gallery)
        {
            if (getGallery())
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean getGalleryPhoto()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), REQUEST_GALLERY_IMAGE);
        startActivityForResult(intent, REQUEST_GALLERY_IMAGE);

        return true;
    }

    public boolean getGallery()
    {
        Intent intent = new Intent(this, galleryListActivity.class);
        startActivityForResult(intent, REQUEST_GALLERY);

        return true;
    }

    public boolean takePhoto()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            ImageRepository repository = new ImageRepository(getApplicationContext());
            photoFile = repository.createImageFile();

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mLastPicture = Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (mLastPicture != null)
            {
                if (mCurrentLocation != null)
                    setFileLocation(mLastPicture.getPath(), mCurrentLocation);

                galleryAddPic(mLastPicture);
            }
        }
        else if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK)
        {
            try {
                Uri _uri = data.getData();

                if (_uri != null) {

                    try {
                        //User had pick an image.
                        Cursor cursor = getContentResolver().query(_uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                        cursor.moveToFirst();

                        do {

                            //Link to the image
                            final String imageFilePath = cursor.getString(0);


                            if (imageFilePath != null)
                                addPhotoToMap(imageFilePath);

                        } while (cursor.moveToNext());

                        cursor.close();
                    }
                    catch (Exception e)
                    {
                        String s = _uri.getPath();
                        if (s != null)
                            addPhotoToMap(s);
                    }
                } else {
                    ClipData clipdata = data.getClipData();
                    if (clipdata != null) {
                        for (int i = 0; i < clipdata.getItemCount(); i++) {
                            ClipData.Item item = clipdata.getItemAt(i);
                            if (item != null) {
                                _uri = item.getUri();
                                if (_uri != null) {
                                    Cursor cursor = getContentResolver().query(_uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                                    cursor.moveToFirst();

                                    do {

                                        //Link to the image
                                        final String imageFilePath = cursor.getString(0);


                                        if (imageFilePath != null)
                                            addPhotoToMap(imageFilePath);

                                    } while (cursor.moveToNext());

                                    cursor.close();
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                String s = e.getLocalizedMessage();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
            }
        }
        else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK)
        {
            String folderName = data.getStringExtra("folder_name");
            if (folderName != null)
            {
                File[] files = ImageRepository.getFolderContent(folderName);
                if (files != null)
                {
                    for (File file : files)
                    {
                        try{
                            addPhotoToMap(file.getCanonicalPath());
                        }
                        catch (IOException e)
                        {

                        }
                    }
                }
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MapsActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            mPhotoInfoWindowAdapter = new photoInfoWindowAdapter(this);

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setInfoWindowAdapter(mPhotoInfoWindowAdapter);

                setUpMap();

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        geoLocation location = findLocationFor(marker);
                        if (location == null) {
                            CameraUpdate center= CameraUpdateFactory.newLatLng(marker.getPosition());
                            mMap.animateCamera(center, 1000, null);
                            return true;
                        }
                        else {

                            selectInfoMarker(marker, true);

                            CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                            mMap.animateCamera(center, 1000, null);

                            marker.showInfoWindow();
                            return true;
                        }
                    }

                });

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        photoInfoWindowAdapter.markerType type = mPhotoInfoWindowAdapter.getMarkerType(marker);

                        if (type == photoInfoWindowAdapter.markerType.INFO)
                        {
                            geoLocation location = findLocationFor(marker);

                            if (location != null)
                            {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                String lang = getResources().getString(R.string.wiki_lang);
                                String buffer = String.format("https://%s.wikipedia.org?curid=%s", lang, location.mPageId);
                                intent.setData(Uri.parse(buffer));

                                startActivity(intent);
                            }
                        }
                        else if (type == photoInfoWindowAdapter.markerType.PHOTO)
                        {
                            CameraUpdate center= CameraUpdateFactory.newLatLng(marker.getPosition());
                            mMap.moveCamera(center);

                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                        }
                    }
                });
             }
         }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        onUpdateLocations(mGeoLocationvalues, null);
        onUpdatePhotos();

        if (mCurrentLocation != null)
        {

            CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            mMap.moveCamera(center);

            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        }
    }

    void onUpdateLocations(ArrayList<geoLocation> values, geoLocation activeLocation)
    {
        mPhotoInfoWindowAdapter.removeMapInfoMarkers();

        mGeoLocationvalues.clear();
        mGeoLocationvalues.addAll(values);

        for (geoLocation loc : mGeoLocationvalues )
        {
                MarkerOptions marker = new MarkerOptions();
                marker.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
                marker.title(loc.getProvider());
                marker.draggable(false);

                if (activeLocation == null)
                      marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                else {
                    if (activeLocation.mPageId == loc.mPageId)
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    else
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                }

                Marker mark = mMap.addMarker(marker);
                if (mark != null)
                    mPhotoInfoWindowAdapter.addMapInfoMarker(mark);
        }
    }

    geoLocation findLocationFor(Marker marker)
    {
        for (geoLocation location : mGeoLocationvalues)
        {
            if (location.getProvider().equals(marker.getTitle()))
                return location;
        }

        return null;
    }

    void selectInfoMarker(Marker marker, boolean deselectOthers)
    {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        if (deselectOthers == true) {
            for (Marker marker1 : mPhotoInfoWindowAdapter.mMapInfoMarkersValues) {
                if (marker1.getTitle().equals(marker.getTitle()) == false)
                    marker1.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

            }
        }
    }

    public void searchForData()
    {
        if (mCurrentLocation != null)
            mNavigationDrawerFragment.searchForData(mCurrentLocation);
    }


    void onUpdatePhotos()
    {
        mPhotoInfoWindowAdapter.removeMapPhotoMarkers();

        ImageRepository repository = new ImageRepository(getApplicationContext());
        File[] files = repository.getPhotoList();
        if (files == null)
            return;

        for (File file : files)
        {
            try{
                addPhotoToMap(file.getCanonicalPath());
            }
            catch (IOException e)
            {

            }
        }

    }

    private void addPhotoToMap(String filename)
    {
        try
        {
            ExifInterface exifData = new ExifInterface(filename);

            geoDegree geo = new geoDegree(exifData);

            if (geo.isValid()) {
                float latitude = geo.getLatitude();
                float longitude = geo.getLongitude();

                byte[] thumbnail =  exifData.getThumbnail();
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
                if (bitmapImage == null)
                    return;

                View view = getLayoutInflater().inflate(R.layout.photo_marker, null);

                ImageView preview = (ImageView)view.findViewById(R.id.imageView);
                if (preview != null)
                {
                     if (bitmapImage != null)
                        preview.setImageBitmap(bitmapImage);
                }

                view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                Bitmap bm = view.getDrawingCache();

                MarkerOptions place = new MarkerOptions()
                                       .title(getResources().getString(R.string.photo_preview))
                                       .position(new LatLng(latitude, longitude))
                                       //.icon(BitmapDescriptorFactory.fromResource(R.drawable.null_icon));
                                        .icon(BitmapDescriptorFactory.fromBitmap(bm));

                Marker marker = mMap.addMarker(place);
                if (marker != null) {
                    mPhotoInfoWindowAdapter.addMapPhotoMarker(marker, bitmapImage);
                   // marker.showInfoWindow();
                }

            }
            else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.pict_loc_error), Toast.LENGTH_SHORT).show();
        }
        catch (IOException exc)
        {

        }
        catch (Exception e)
        {

        }
    }

    private void setFileLocation(String filename, Location location)
    {
        try {
            ExifInterface exifData = new ExifInterface(filename);

            geoDegree geo = new geoDegree(exifData);

            if (geo.isValid() == false) {
                geoDegree.saveData(filename, location.getLatitude(), location.getLongitude());
            }
        }
        catch (IOException exc)
        {

        }
    }
    private void galleryAddPic(Uri uri) {
        addPhotoToMap(uri.getPath());

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        this.sendBroadcast(mediaScanIntent);
    }

}
