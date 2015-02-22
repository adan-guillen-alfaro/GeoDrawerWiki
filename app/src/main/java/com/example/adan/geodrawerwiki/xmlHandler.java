package com.example.adan.geodrawerwiki;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

class geoLocation extends Location implements Parcelable
{
    public geoLocation(String title, double lon, double lat, double dist, String pageId)
    {
        super(title);

        setLatitude(lat);
        setLongitude(lon);

        mDistance = dist;
        mPageId = pageId;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getProvider());
        out.writeDouble(getLongitude());
        out.writeDouble(getLatitude());
        out.writeDouble(mDistance);
        out.writeString(mPageId);
    }

    public static final Creator<geoLocation> CREATOR
            = new Creator<geoLocation>() {
        public geoLocation createFromParcel(Parcel in) {
            return new geoLocation(in);
        }

        public geoLocation[] newArray(int size) {
            return new geoLocation[size];
        }
    };

    private geoLocation(Parcel in) {
        super(in.readString());

        setLongitude(in.readDouble());
        setLatitude(in.readDouble());
        mDistance = in.readDouble();
        mPageId = in.readString();
    }

    public double mDistance;
    public String mPageId;
};

/**
 * Created by Adan on 28/12/14.
 */
class xmlHandlerWiki {

    private static final String VALUE_TAG = "gs";
    private static final String LONGITUDE_ATT = "lon";
    private static final String LATITUDE_ATT = "lat";
    private static final String TITLE_ATT = "title";
    private static final String DIST_ATT = "dist";
    private static final String PAGEID_ATT = "pageid";

    private String mBuffer;
    private final ArrayList<geoLocation> mResults = new ArrayList<geoLocation>();

    public xmlHandlerWiki(String s)
    {
        mBuffer = s;
    }

    public ArrayList<geoLocation> process()
    {
        try {

            // Create the Pull Parser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(mBuffer));

            // Get the first Parser event and start iterating over the XML document
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    processTag(xpp);
                } else if (eventType == XmlPullParser.END_TAG) {
                } else if (eventType == XmlPullParser.TEXT) {
                }
                eventType = xpp.next();
            }
            return mResults;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mResults;
    }

    private void processTag(XmlPullParser xpp)
    {
        if (xpp.getName().equals(VALUE_TAG))
        {
            try
            {
                String lon = xpp.getAttributeValue(null, LONGITUDE_ATT);
                String lat = xpp.getAttributeValue(null, LATITUDE_ATT);
                String title = xpp.getAttributeValue(null, TITLE_ATT);
                String dist = xpp.getAttributeValue(null, DIST_ATT);
                String pageId = xpp.getAttributeValue(null, PAGEID_ATT);

                geoLocation location = new geoLocation(title, Double.parseDouble(lon), Double.parseDouble(lat), Double.parseDouble(dist), pageId);
                mResults.add(location);
            }
            catch (Exception e) {

            }

        }

    }
}
