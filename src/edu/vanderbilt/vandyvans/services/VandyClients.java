package edu.vanderbilt.vandyvans.services;

import android.os.Handler;

/**
 * Interface for accessing the services. RoboGuice will inject an object
 * that implements this interface.
 *
 * Created by athran on 3/18/14.
 */
public interface VandyClients {

    /**
     * VandyVansClient Singleton provides the hook for performing http
     * request to the vandyvans.com API.
     *
     * Messages:
     *     `FetchStops`
     *     `FetchWaypoints`
     *     `Report`
     *
     */
    Handler vandyVans();

    /**
     * SyncromaticsClient Singleton provides the hook for performing
     * http request to http://api.syncromatics.com/.
     */
    Handler syncromatics();

}
