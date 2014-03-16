package edu.vanderbilt.vandyvans.models;

public final class Van {

    public final int       id;
    public final int       percentFull;
    public final FloatPair location;
    
    public Van(int _id, int _percentFull, FloatPair _loc) {
        id          = _id;
        percentFull = _percentFull;
        location    = _loc;
    }
    
    public static final String TAG_ID           = "ID";
    public static final String TAG_PERCENT_FULL = "APCPercentage";
    public static final String TAG_LATS         = "Latitude";
    public static final String TAG_LOND         = "Longitude";
    
}
