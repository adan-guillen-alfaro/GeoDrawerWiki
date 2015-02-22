package com.example.adan.geodrawerwiki;

import android.location.Location;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by Adan on 17/1/15.
 */
public class geoDegree {
    private boolean valid = false;
    Float Latitude, Longitude;

    geoDegree(ExifInterface exif) {
        String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

        if ((attrLATITUDE != null)
                && (attrLATITUDE_REF != null)
                && (attrLONGITUDE != null)
                && (attrLONGITUDE_REF != null)) {
            valid = true;

            if (attrLATITUDE_REF.equals("N")) {
                Latitude = convertToDegree(attrLATITUDE);
            } else {
                Latitude = 0 - convertToDegree(attrLATITUDE);
            }

            if (attrLONGITUDE_REF.equals("E")) {
                Longitude = convertToDegree(attrLONGITUDE);
            } else {
                Longitude = 0 - convertToDegree(attrLONGITUDE);
            }

        }
    };

    static void saveData(String filename, double latitude, double longitude)
    {
        try {
            ExifInterface exif = new ExifInterface(filename);

            double alat = Math.abs(latitude);
            double along = Math.abs(longitude);
//            String stringLati = convertDoubleIntoDegree(alat);
//            String stringLongi = convertDoubleIntoDegree(along);

            String stringLati = Location.convert(alat, Location.FORMAT_DEGREES);
            String stringLongi = Location.convert(along, Location.FORMAT_DEGREES);

            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, stringLati);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, stringLongi);

            exif.saveAttributes();
            String lati = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String longi = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        }
        catch (IOException excp)
        {

        }
    }

    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;


    }

    public boolean isValid()
    {
        return valid;
    }

    public float getLatitude()
    {
        return Latitude;
    }

    public float getLongitude()
    {
        return Longitude;
    }
};