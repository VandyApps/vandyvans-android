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
import com.google.android.gms.maps.model.PolylineOptionsCreator;

import edu.vanderbilt.vandyvans.models.FloatPair;
import edu.vanderbilt.vandyvans.models.Route;
import edu.vanderbilt.vandyvans.models.Routes;
import edu.vanderbilt.vandyvans.models.Stop;
import edu.vanderbilt.vandyvans.models.Van;
import edu.vanderbilt.vandyvans.services.Global;
import edu.vanderbilt.vandyvans.services.VandyClients;

import static edu.vanderbilt.vandyvans.services.Global.APP_LOG_ID;

/**
 *
 * Created by athran on 3/19/14.
 */
public class MapController implements Handler.Callback,
                                      View.OnClickListener {

    public static final int    DEFAULT_ZOOM = 15;
    public static final String LOG_ID       = "MapController";

    private final Handler      bridge = new Handler(this);
    private final VandyClients clients;

    private final SupportMapFragment mMapFragment;
    private final LinearLayout       mOverlayBar;
    private final Button             mBlueButton;
    private final Button             mRedButton;
    private final Button             mGreenButton;

    public MapController(SupportMapFragment mapFrag,
                         LinearLayout       overlayBar,
                         Button             blueBtn,
                         Button             redBtn,
                         Button             greenBtn,
                         VandyClients       _clients) {
        if (mapFrag == null) { throw new IllegalStateException("MapFragment is null"); }
        mMapFragment = mapFrag;

        mOverlayBar  = overlayBar;
        mBlueButton  = blueBtn;
        mRedButton   = redBtn;
        mGreenButton = greenBtn;

        mBlueButton  .setOnClickListener(this);
        mRedButton   .setOnClickListener(this);
        mGreenButton .setOnClickListener(this);

        clients = _clients;
    }

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

    public void routeSelected(Route route) {
        if (clients == null) {
            throw new IllegalStateException("VandyClient is null");
        }
        Message.obtain(clients.vandyVans(), 0,
                       new Global.FetchWaypoints(bridge, route))
                .sendToTarget();

        Message.obtain(clients.vandyVans(), 0,
                       new Global.FetchStops(bridge, route))
                .sendToTarget();

        Message.obtain(clients.syncromatics(), 0,
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

    }

    private boolean drawWaypoints(Global.WaypointResults result) {


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
                                  .draggable(false)
                                  .flat(true)
                                  .icon(BitmapDescriptorFactory
                                                .fromResource(R.drawable.van_icon))
                                  .anchor(0.5f,0.5f));
        }

        return true;
    }

    public void showOverlay() {
        mOverlayBar.setVisibility(View.VISIBLE);
    }

    public void hideOverlay() {
        mOverlayBar.setVisibility(View.INVISIBLE);
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
}
