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

public final class Global extends android.app.Application {

    /**
     * VandyVansClient Singleton provide the hook for performing http
     * request to the VandyVans.com API.
     */
    private static Handler sVandyVansClient = null;
    public static Handler vandyVansClient() { return sVandyVansClient; }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Global.initializeGlobalState(this);
    }

    /**
     * Should be called the a subclass of Application.
     * @param ctx
     */
    public static void initializeGlobalState(Context ctx) {
        HandlerThread thread = new HandlerThread("BackgroundThread");
        thread.start();
        sVandyVansClient = new Handler(thread.getLooper(), new VandyVansClient());
        sVandyVansClient.sendMessage(Message.obtain(null, 0, new Initialize(ctx)));
        
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
    
    private static final class VandyVansClient implements Handler.Callback {

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
                return fetchWaypoints(
                        ((FetchWaypoints) msg.obj).from,
                        ((FetchWaypoints) msg.obj).route);
            else return false;
        }
        
        private boolean init() {
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
                Log.e("VandyVansClient", e.getMessage());
                return false;
            }
            return true;
        }
        
        private boolean fetchWaypoints(Handler from, Route r) {
            return true;
        }
    }
    
    private static final class SyncromaticsClient implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            return false;
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
