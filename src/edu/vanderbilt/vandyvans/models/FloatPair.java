package edu.vanderbilt.vandyvans;

public final class FloatPair {

    public final double lat;
    public final double lon;
    
    public FloatPair(double _lat, double _lon) {
        lat = _lat;
        lon = _lon;
    }

    public static final String TAG_LAT = "Latitude";
    public static final String TAG_LON = "Longitude";
    
}
