package edu.vanderbilt.vandyvans.models;

public final class Stop {
    
    public final int    id;
    public final String name;
    public final String image;
    public final double latitude;
    public final double longitude;
    public final int    rtpi;

    public Stop(
            int    _id, 
            String _name, 
            String _image, 
            double _lat,
            double _lon,
            int    _rtpi) {
        id        = _id;
        name      = _name;
        image     = _image;
        latitude  = _lat;
        longitude = _lon;
        rtpi      = _rtpi;
    }
    
    public static final String TAG_ID    = "ID";
    public static final String TAG_IMAGE = "Image";
    public static final String TAG_LAT   = "Latitude";
    public static final String TAG_LON   = "Longitude";
    public static final String TAG_NAME  = "Name";
    public static final String TAG_RTPI  = "RtpiNumber";

}
