package edu.vanderbilt.vandyvans;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

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
    private static Handler sVandyVansClient = null;
    public static Handler vandyVansClient() { return sVandyVansClient; }
    
    /**
     * SyncromaticsClient Singleton provides the hook for performing
     * http request to http://api.syncromatics.com/.
     */
    private static Handler sSyncromatics = null;
    public static Handler syncromaticsClient() { return sSyncromatics; }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Global.initializeGlobalState(this);
    }

    /**
     * Should be called by a subclass of Application.
     * @param ctx
     */
    public static void initializeGlobalState(Context ctx) {
        final HandlerThread thread = new HandlerThread("BackgroundThread");
        thread.start();
        sVandyVansClient = new Handler(thread.getLooper(), new VandyVansClient());
        sVandyVansClient.sendMessage(Message.obtain(null, 0, new Initialize(ctx)));
        
        sSyncromatics = new Handler(thread.getLooper(), new SyncromaticsClient());
        sSyncromatics.sendMessage(Message.obtain(null, 0, new Initialize(ctx)));
    }

    /**
     * Signal for requesting Stops data from the VandyVans.com API.
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
    
    public static final class Stops {
        public final List<Stop> stops;
        public Stops(List<Stop> list) {
            stops = list;
        }
    }
    
    public static final class Waypoints {
        public final List<FloatPair> waypoints;
        public Waypoints(List<FloatPair> _waypoints) {
            waypoints = _waypoints;
        }
    }
    
    private static final class VandyVansClient implements Handler.Callback {

        private static final String LOG_TAG = "VandyVansClient";
        private static final String BASE_URL = "http://vandyvans.com/";

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.obj instanceof Initialize) 
                return init();
            
            else if (msg.obj instanceof FetchStops) 
                return fetchStops(
                        ((FetchStops) msg.obj).from, 
                        ((FetchStops) msg.obj).route);
            
            else if (msg.obj instanceof FetchWaypoints)
                return waypoints(
                        ((FetchWaypoints) msg.obj).from,
                        ((FetchWaypoints) msg.obj).route);
            
            else return false;
        }
        
        private boolean init() {
            Log.d(LOG_TAG, "Initialization");
            return true;
        }
        
        private boolean fetchStops(Handler from, Route r) {

            StringBuilder buffer = new StringBuilder(BASE_URL)
                    .append("Route/")
                    .append(r.id)
                    .append("/Direction/0/Stops");
            
            try {
                Reader reader = new InputStreamReader(Global.get(buffer.toString()));
                List<Stop> result = new LinkedList<Stop>();
                for (JsonElement elem : new JsonParser().parse(reader).getAsJsonArray()) {
                    JsonObject obj = elem.getAsJsonObject();
                    result.add(new Stop(
                            obj.get(Stop.TAG_ID).getAsInt(),
                            obj.get(Stop.TAG_NAME).getAsString(),
                            obj.get(Stop.TAG_IMAGE).getAsString(),
                            obj.get(Stop.TAG_LAT).getAsDouble(),
                            obj.get(Stop.TAG_LON).getAsDouble(),
                            obj.get(Stop.TAG_RTPI).getAsInt()));
                }
                
                reader.close();
                from.sendMessage(from.obtainMessage(0, new Stops(result)));
                
            } catch (Exception e) {
                Log.e(LOG_TAG, "Failed to get Stops for Route.");
                Log.e(LOG_TAG, "URL: " + buffer.toString());
                Log.e(LOG_TAG, e.getMessage());
            }
            return true;
        }
        
        private boolean waypoints(Handler from, Route r) {
            
            StringBuilder buffer = new StringBuilder(BASE_URL)
                    .append("Route/")
                    .append(r.id)
                    .append("/Waypoints");
            
            try {
                Reader reader = new InputStreamReader(Global.get(buffer.toString()));
                List<FloatPair> result = new LinkedList<FloatPair>();
                for (JsonElement elem : new JsonParser().parse(reader).getAsJsonArray()) {
                    JsonObject obj = elem.getAsJsonObject();
                    result.add(new FloatPair(
                            obj.get(FloatPair.TAG_LAT).getAsDouble(),
                            obj.get(FloatPair.TAG_LON).getAsDouble()));
                }
                
                reader.close();
                from.sendMessage(from.obtainMessage(0, new Waypoints(result)));
                
            } catch (Exception e) {
                Log.e(LOG_TAG, "Failed to get Waypoints for Route.");
                Log.e(LOG_TAG, "URL: " + buffer.toString());
                Log.e(LOG_TAG, e.getMessage());
            }
            return true;
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
    
    private static final class SyncromaticsClient implements Handler.Callback {

        private static final String LOG_TAG = "SyncromaticsClient";
        private static final String BASE_URL = "http://api.syncromatics.com/";
        private static final String API_KEY = "a922a34dfb5e63ba549adbb259518909";

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.obj instanceof Initialize)
                return init();
            
            else if (msg.obj instanceof FetchVans)
                return vans(
                        ((FetchVans) msg.obj).from,
                        ((FetchVans) msg.obj).route);
            
            else if (msg.obj instanceof FetchArrivalTimes)
                return arrivalTimes(
                        ((FetchArrivalTimes) msg.obj).from,
                        ((FetchArrivalTimes) msg.obj).stop);
            
            else return false;
        }
        
        private boolean init() {
            Log.d(LOG_TAG, "Initialization");
            return true;
        }
        
        private boolean vans(Handler requester, Route route) {
            StringBuilder buffer = new StringBuilder(BASE_URL)
                    .append("Route/")
                    .append(route.id)
                    .append("/Vehicles?api_key=")
                    .append(API_KEY);
            
            try {
                Reader reader = new InputStreamReader(get(buffer.toString()));
                List<Van> result = new LinkedList<Van>();
                for (JsonElement elem : new JsonParser().parse(reader).getAsJsonArray()) {
                    JsonObject obj = elem.getAsJsonObject();
                    result.add(new Van(
                            obj.get(Van.TAG_ID).getAsInt(),
                            obj.get(Van.TAG_PERCENT_FULL).getAsInt(),
                            new FloatPair(
                                    obj.get(Van.TAG_LATS).getAsDouble(), 
                                    obj.get(Van.TAG_LOND).getAsDouble())));
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Failed to get Vans for Route.");
                Log.e(LOG_TAG, "URL: " + buffer.toString());
                Log.e(LOG_TAG, e.getMessage());
            }
            return true;
        }
        
        private boolean arrivalTimes(Handler requester, Stop stop) {
            return true;
        }
    }
    
    private static final class Initialize {
        final Context ctx;
        public Initialize(Context _ctx) {
            ctx = _ctx;
        }
    }
    
    private static InputStream get(String url) throws IOException {
        return new URL(url).openStream();
    }
    
}
