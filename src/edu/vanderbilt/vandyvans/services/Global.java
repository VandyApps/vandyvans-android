package edu.vanderbilt.vandyvans.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import edu.vanderbilt.vandyvans.R;
import edu.vanderbilt.vandyvans.models.Routes;
import roboguice.RoboGuice;

import edu.vanderbilt.vandyvans.models.ArrivalTime;
import edu.vanderbilt.vandyvans.models.FloatPair;
import edu.vanderbilt.vandyvans.models.Route;
import edu.vanderbilt.vandyvans.models.Stop;
import edu.vanderbilt.vandyvans.models.Van;

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

    //https://maps.google.com/?ll=36.143905,-86.805811&spn=0.012545,0.024097&t=h&z=16
    public static final double DEFAULT_LONGITUDE = -86.805811;
    public static final double DEFAULT_LATITUDE  = 36.143905;
    public static final String APP_LOG_ID        = "VandyVans";

    private VandyClientsSingleton mClientSingleton;

    private int COLOR_RED;
    private int COLOR_BLUE;
    private int COLOR_GREEN;
    private int COLOR_BLACK;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeGlobalState();

        Resources res = getResources();
        COLOR_BLUE  = res.getColor(R.color.blue_argb);
        COLOR_RED   = res.getColor(R.color.red_argb);
        COLOR_GREEN = res.getColor(R.color.green_argb);
        COLOR_BLACK = res.getColor(android.R.color.black);
    }

    public int getRed() { return COLOR_RED; }

    public int getBlue() { return COLOR_BLUE; }

    public int getGreen() { return COLOR_GREEN; }

    public int getColorFor(Route route) {
        if (route == Routes.BLUE)  return COLOR_BLUE;
        if (route == Routes.RED)   return COLOR_RED;
        if (route == Routes.GREEN) return COLOR_GREEN;
        return COLOR_BLACK;
    }

    public Global setShowSelfLocation(boolean showLocation) {
        return this;
    }

    public boolean isShowingLocation() {
        return true;
    }

    /**
     * Called in the `onCreate` of android.app.Application when the process
     * for this app is first created. This will initialize the service
     * infrastructure that provide data to the UI.
     */
    private void initializeGlobalState() {

        // Intialize the background thread to be used by the services.
        final HandlerThread thread = new HandlerThread("BackgroundThread");
        thread.start();

        // Create an Object to hold on to the services.
        mClientSingleton = new VandyClientsSingleton(thread, this);

        // Create a provider to inject the Service Holder to anybody who
        // needs it.
        RoboGuice.setBaseApplicationInjector(
                this,
                RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this),
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                        binder.bind(VandyClients.class)

                                // inject the fucking injector!
                                .toProvider(new Provider<VandyClients>() {
                                    @Override
                                    public VandyClients get() {
                                        return mClientSingleton;
                                    }
                                });

                        binder.bind(Global.class)
                                .toProvider(new Provider<Global>() {
                                    @Override
                                    public Global get() {
                                        return Global.this;
                                    }
                                });
                    }
                });

    }

    private static final class VandyClientsSingleton implements VandyClients {

        final Handler vandyVansClient;
        final Handler syncromaticsClient;

        VandyClientsSingleton(HandlerThread serviceThread, Context ctx) {
            vandyVansClient = new Handler(serviceThread.getLooper(),
                                          new VandyVansClient());
            Message.obtain(vandyVansClient, 0,
                           new Initialize(ctx)).sendToTarget();

            syncromaticsClient = new Handler(serviceThread.getLooper(),
                                             new SyncromaticsClient());
            Message.obtain(syncromaticsClient, 0,
                           new Initialize(ctx)).sendToTarget();
        }

        @Override
        public Handler vandyVans() {
            return vandyVansClient;
        }

        @Override
        public Handler syncromatics() {
            return syncromaticsClient;
        }
    }

    /**
     * Signal for requesting Stop data from the VandyVans.com API.
     *
     * Reply: `StopResults`
     *
     * @author athran
     */
    public static final class FetchStops {
        public final Route   route;
        public final Handler from;
        public FetchStops(Handler _from, Route _r) {
            route = _r;
            from  = _from;
        }
    }

    /**
     * To:    VandyVans Client
     * Reply: `WaypointResults`
     */
    public static final class FetchWaypoints {
        public final Route   route;
        public final Handler from;
        public FetchWaypoints(Handler _from, Route _r) {
            route = _r;
            from  = _from;
        }
    }
    
    public static final class StopResults {
        public final List<Stop> stops;
        public StopResults(List<Stop> list) {
            stops = list;
        }
    }
    
    public static final class WaypointResults {
        public final List<FloatPair> waypoints;
        public WaypointResults(List<FloatPair> _waypoints) {
            waypoints = _waypoints;
        }
    }

    /**
     * To:    Syncromatics Client
     * Reply: `VanResults`
     */
    public static final class FetchVans {
        public final Route   route;
        public final Handler from;
        public FetchVans(Handler _from, Route _r) {
            route = _r;
            from  = _from;
        }
    }

    /**
     * To:    Syncromatics Client
     * Reply: `ArrivalTimeResults`
     */
    public static final class FetchArrivalTimes {
        public final Stop    stop;
        public final Handler from;
        public FetchArrivalTimes(Handler _from, Stop _stop) {
            stop = _stop;
            from = _from;
        }
    }
    
    public static final class VanResults {
        public final List<Van> vans;
        public VanResults(List<Van> _vans) {
            vans = _vans;
        }
    }
    
    public static final class ArrivalTimeResults {
        public final List<ArrivalTime> times;
        public ArrivalTimeResults(List<ArrivalTime> _times) {
            times = _times;
        }
    }

    public static final class Failure {
        public final Object    originalMessage;
        public final Exception error;
        public final String    extraInfo;
        public Failure(Object _msg,
                       Exception _error,
                       String _info) {
            originalMessage = _msg;
            error           = _error;
            extraInfo       = _info;
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

    static InputStream post(String url, String params) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        Writer writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(params);
        writer.flush();

        return conn.getInputStream();
    }

    static InputStream postUrlRequest(String url, Map<String,String> params) throws IOException {
        StringBuilder builder = new StringBuilder(url);
        builder.append("?");

        for (String key : params.keySet()) {
            builder
                    .append(key)
                    .append("=")
                    .append(URLEncoder.encode(params.get(key),
                                              "UTF-8"))
                    .append("&");
        }

        builder.deleteCharAt(builder.length()-1);

        Log.i("VandyVansClient", builder.toString());
        URLConnection conn = new URL(builder.toString()).openConnection();
        conn.setDoInput(true);
        conn.setUseCaches(false);

        return conn.getInputStream();
    }

    static String encryptPassword(String password)
    {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }

    static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

}
