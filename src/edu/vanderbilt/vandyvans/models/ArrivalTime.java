package edu.vanderbilt.vandyvans.models;

/**
 * Created by athran on 3/15/14.
 */
public class ArrivalTime {

    public final Stop  stop;
    public final Route route;
    public final int   minutes;

    public ArrivalTime(Stop  _stop,
                       Route _route,
                       int   _min) {
        stop    = _stop;
        route   = _route;
        minutes = _min;
    }

}
