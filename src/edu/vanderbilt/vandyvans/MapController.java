package edu.vanderbilt.vandyvans;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.vanderbilt.vandyvans.models.FloatPair;
import edu.vanderbilt.vandyvans.models.Route;
import edu.vanderbilt.vandyvans.models.Routes;
import edu.vanderbilt.vandyvans.models.Stop;
import edu.vanderbilt.vandyvans.models.Van;
import edu.vanderbilt.vandyvans.services.Global;
import edu.vanderbilt.vandyvans.services.VandyClients;

import static edu.vanderbilt.vandyvans.services.Global.APP_LOG_ID;

/**
 * Controls the behaviour of the map. Usually the activity would act as
 * a controller, but we don't want to clutter the StopActivity with
 * too much responsibility.
 *
 * This Controller is a master of the SupportMapFragment that fills the
 * second slot of the ViewPager, the LinearLayout that is the bottom bar,
 * and the three Buttons for selecting Routes. It captures the click
 * events from the Buttons, fetches data from the services, and manipulates
 * the map in response to the data.
 *
 * Created by athran on 3/19/14.
 */
public class MapController implements Handler.Callback,
                                      View.OnClickListener {

    public static final float  DEFAULT_ZOOM  = 14.5f;
    public static final float  DEFAULT_WIDTH = 5;
    public static final String LOG_ID        = "MapController";

    private final Handler      bridge = new Handler(this);
    private final VandyClients mClients;

    private final SupportMapFragment mMapFragment;
    private final LinearLayout       mOverlayBar;
    private final Button             mBlueButton;
    private final Button             mRedButton;
    private final Button             mGreenButton;

    private Route mCurrentRoute;

    /**
     * The sole constructor.
     *
     * @param mapFrag
     * @param overlayBar
     * @param blueBtn
     * @param redBtn
     * @param greenBtn
     * @param clients
     */
    public MapController(SupportMapFragment mapFrag,
                         LinearLayout       overlayBar,
                         Button             blueBtn,
                         Button             redBtn,
                         Button             greenBtn,
                         VandyClients       clients) {
        if (mapFrag == null) { throw new IllegalStateException("MapFragment is null"); }
        mMapFragment = mapFrag;

        mOverlayBar  = overlayBar;
        mBlueButton  = blueBtn;
        mRedButton   = redBtn;
        mGreenButton = greenBtn;

        mBlueButton  .setOnClickListener(this);
        mRedButton   .setOnClickListener(this);
        mGreenButton .setOnClickListener(this);

        mClients      = clients;
        mCurrentRoute = Routes.BLUE;
    }

    /**
     * Handles the messages from the Services. Whenever the UI need any data, it will
     * send a request to the Services. The Services will process the request, fetching
     * the data from whatever source it has access to, then reply with the data.
     *
     * See: `routeSelected(route)`
     *
     * @param message
     * @return
     */
    @Override
    public boolean handleMessage(Message message) {
        if (message.obj instanceof Global.WaypointResults)
            return drawWaypoints((Global.WaypointResults) message.obj);

        if (message.obj instanceof Global.StopResults)
            return drawStops((Global.StopResults) message.obj);

        if (message.obj instanceof Global.VanResults)
            return drawVans((Global.VanResults) message.obj);

        return false;
    }

    /**
     * This method is called when one of the overlay buttons is clicked.
     * Send messages to the Services, requesting data on Stops, route path,
     * and van locations. Then clear and center the map.
     *
     * See: `onClick(view)`
     *
     * @param route
     */
    public void routeSelected(Route route) {
        if (mClients == null) {
            throw new IllegalStateException("VandyClient is null");
        }

        mCurrentRoute = route;

        Message.obtain(mClients.vandyVans(), 0,
                       new Global.FetchWaypoints(bridge, route))
                .sendToTarget();

        Message.obtain(mClients.vandyVans(), 0,
                       new Global.FetchStops(bridge, route))
                .sendToTarget();

        Message.obtain(mClients.syncromatics(), 0,
                       new Global.FetchVans(bridge, route))
                .sendToTarget();

        GoogleMap map = mMapFragment.getMap();
        if (map == null) { return; }

        map.clear();
        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(Global.DEFAULT_LATITUDE,
                                   Global.DEFAULT_LONGITUDE),
                        DEFAULT_ZOOM
                ));
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if (view == mBlueButton) {
            mOverlayBar.setBackgroundColor(
                    view.getResources()
                            .getColor(R.color.blue));
            routeSelected(Routes.BLUE);

        } else if (view == mRedButton) {
            mOverlayBar.setBackgroundColor(
                    view.getResources()
                            .getColor(R.color.red));
            routeSelected(Routes.RED);

        } else if (view == mGreenButton) {
            mOverlayBar.setBackgroundColor(
                    view.getResources()
                            .getColor(R.color.green));
            routeSelected(Routes.GREEN);
        }
    }

    public void showOverlay() {
        mOverlayBar.setVisibility(View.VISIBLE);
    }

    public void hideOverlay() {
        mOverlayBar.setVisibility(View.INVISIBLE);
    }

    public void mapIsShown() {
        routeSelected(mCurrentRoute);
    }

    private boolean drawWaypoints(Global.WaypointResults result) {
        GoogleMap map = mMapFragment.getMap();
        if (map == null) { return true; }

        PolylineOptions polyline = new PolylineOptions();
        polyline.color(
                (mCurrentRoute == Routes.BLUE)  ? 0xff0000ff :
                (mCurrentRoute == Routes.RED)   ? 0xffff0000 :
                (mCurrentRoute == Routes.GREEN) ? 0xff00ff00 :
                0xff000000);
        polyline.width(DEFAULT_WIDTH);

        for (FloatPair point : result.waypoints) {
            polyline.add(new LatLng(point.lat,
                                    point.lon));
        }

        map.addPolyline(polyline);
        return true;
    }

    private boolean drawStops(Global.StopResults result) {
        GoogleMap map = mMapFragment.getMap();
        if (map == null) { return true; }

        for (Stop stop : result.stops) {
            map.addMarker(new MarkerOptions()
                                  .position(new LatLng(stop.latitude,
                                                       stop.longitude))
                                  .title(stop.name)
                                  .draggable(false));
        }

        return true;
    }

    private boolean drawVans(Global.VanResults result) {
        GoogleMap map = mMapFragment.getMap();
        if (map == null) { return true; }

        Log.i(APP_LOG_ID, LOG_ID + " | Received this many Van results: " + result.vans.size());
        for (Van v : result.vans) {
            map.addMarker(new MarkerOptions()
                                  .position(new LatLng(v.location.lat,
                                                       v.location.lon))
                                  .title("" + v.percentFull + "%")
                                  .draggable(false)
                                  .flat(true)
                                  .icon(BitmapDescriptorFactory
                                                .fromResource(R.drawable.van_icon))
                                  .anchor(0.5f, 0.5f));
        }

        return true;
    }

}
