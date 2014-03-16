package edu.vanderbilt.vandyvans.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import edu.vanderbilt.vandyvans.models.*;

/**
 * Holds static references to the vital backend services which are accessible
 * globally in the application. This Singleton-bundle is initialized by the
 * `onCreate` call made by the Android system as soon as the process for this
 * app is created.
 * 
 * @author athran
 *
 */
public final class Global extends android.app.Application {

    /**
     * VandyVansClient Singleton provides the hook for performing http
     * request to the vandyvans.com API.
     */
    public static Handler vandyVansClient() { return sVandyVansClient; }
    private static Handler sVandyVansClient = null;
    
    /**
     * SyncromaticsClient Singleton provides the hook for performing
     * http request to http://api.syncromatics.com/.
     */
    public static Handler syncromaticsClient() { return sSyncromatics; }
    private static Handler sSyncromatics = null;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Global.initializeGlobalState(this);
    }

    /**
     * Should be called by a subclass of Application.
     * @param ctx
     */
    static void initializeGlobalState(Context ctx) {
        final HandlerThread thread = new HandlerThread("BackgroundThread");
        thread.start();
        sVandyVansClient = new Handler(thread.getLooper(), new VandyVansClient());
        sVandyVansClient.sendMessage(Message.obtain(null, 0, new Initialize(ctx)));
        
        sSyncromatics = new Handler(thread.getLooper(), new SyncromaticsClient());
        sSyncromatics.sendMessage(Message.obtain(null, 0, new Initialize(ctx)));
    }

    /**
     * Signal for requesting Stop data from the VandyVans.com API.
     * Send to `Global.vandyVansClient()` and listen for the reply.
     * 
     * @author athran
     */
    public static final class FetchStops {
        public final Route route;
        public final Handler from;
        public FetchStops(Handler _from, Route _r) {
            route = _r;
            from = _from;
        }
    }
    
    public static final class FetchWaypoints {
        public final Route route;
        public final Handler from;
        public FetchWaypoints(Handler _from, Route _r) {
            route = _r;
            from = _from;
        }
    }
    
    public static final class StopResults {
        public final List<Stop> stops;
        public StopResults(List<Stop> list) {
            stops = list;
        }
    }
    
    public static final class Waypoints {
        public final List<FloatPair> waypoints;
        public Waypoints(List<FloatPair> _waypoints) {
            waypoints = _waypoints;
        }
    }

    public static final class FetchVans {
        public final Route route;
        public final Handler from;
        public FetchVans(Handler _from, Route _r) {
            route = _r;
            from = _from;
        }
    }
    
    public static final class FetchArrivalTimes {
        public final Stop stop;
        public final Handler from;
        public FetchArrivalTimes(Handler _from, Stop _stop) {
            stop = _stop;
            from = _from;
        }
    }
    
    public static final class Vans {
        public final List<Van> vans;
        public Vans(List<Van> _vans) {
            vans = _vans;
        }
    }
    
    public static final class ArrivalTimeResults {
        public final List<ArrivalTime> times;
        public ArrivalTimeResults(List<ArrivalTime> _times) {
            times = _times;
        }
    }

    static final class Initialize {
        final Context ctx;
        public Initialize(Context _ctx) {
            ctx = _ctx;
        }
    }
    
    static InputStream get(String url) throws IOException {
        return new URL(url).openStream();
    }
    
}
