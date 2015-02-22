package com.example.adan.geodrawerwiki;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Adan on 3/1/15.
 */
public class GetHttpDataClass {
    public enum eDataType
    {
        WIKIPEDIA,
        GOOGLE,
    };

    private eDataType mDataType = eDataType.WIKIPEDIA;
    private NavigationDrawerFragment mListFragment;
    private MapsActivity mMapsActivity;

    //public static final ArrayList<eDataType> mActiveGeoData = new ArrayList<eDataType>();

    public GetHttpDataClass(NavigationDrawerFragment listFragment, MapsActivity mapsActivity, eDataType type)
    {
        mListFragment = listFragment;
        mMapsActivity = mapsActivity;
        mDataType = type;
    }

    public void execute(final String url)
    {
        Runnable run = null;

        switch (mDataType)
        {
            case WIKIPEDIA:
                run = new Runnable() {
                    @Override
                    public void run() {
                        wikipediaGetHttpData getData = new wikipediaGetHttpData(mListFragment, mMapsActivity);
                        getData.execute(url);

                        Toast.makeText(mMapsActivity.getApplicationContext(), mMapsActivity.getResources().getString(R.string.get_wiki_data), Toast.LENGTH_SHORT).show();
                    }
                };
                break;
            default:
                break;
        }

        if (run != null)
            mMapsActivity.runOnUiThread(run);
    }
};


/**
 * Created by Adan on 26/12/14.
 */
class wikipediaGetHttpData extends AsyncTask<String, Void, String> {

    private Exception exception;

    NavigationDrawerFragment mListFragment = null;
    MapsActivity mMapsActivity = null;
    Context mContext = null;
    wikipediaGetHttpData(NavigationDrawerFragment listFragment, MapsActivity mapsActivity)
    {
        mListFragment = listFragment;
        mMapsActivity = mapsActivity;

        mContext = mMapsActivity.getApplicationContext();
    }

    @Override
    protected String doInBackground(String... urls) {

        try {
            HttpURLConnection httpUrlConn = (HttpURLConnection)new URL(urls[0]).openConnection();
            InputStream input = new BufferedInputStream(httpUrlConn.getInputStream());

            String result = readStream(input);
            return result;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s.length() != 0)
        {
            xmlHandlerWiki handler = new xmlHandlerWiki(s);
            ArrayList<geoLocation> values = handler.process();

            if (mListFragment != null)
                mListFragment.onUpdateList(values, mContext);

            if (mMapsActivity != null)
                mMapsActivity.onUpdateLocations(values, null);

//            if (MainActivity.mProgreesBarLayout != null)
//                MainActivity.mProgreesBarLayout.setVisibility(View.INVISIBLE);
        }
    }


    private String readStream(InputStream in)
    {
        BufferedReader reader = null;
        StringBuffer data = new StringBuffer("");
        try
        {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null)
            {
                data.append(line);
            }
        }
        catch (IOException e)
        {

        }
        finally
        {
            if (reader != null)
            {
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return data.toString();
    }
}