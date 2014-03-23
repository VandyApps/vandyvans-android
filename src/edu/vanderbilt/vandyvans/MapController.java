package edu.vanderbilt.vandyvans;

import android.os.Handler;
import android.os.Message;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptionsCreator;
import com.google.inject.Inject;
import edu.vanderbilt.vandyvans.models.Route;
import edu.vanderbilt.vandyvans.services.Global;
import edu.vanderbilt.vandyvans.services.VandyClients;

/**
 *
 * Created by athran on 3/19/14.
 */
public class MapController implements Handler.Callback {

    final   MapFragment  mMapFragment;
    final   Handler      bridge = new Handler(this);
    @Inject VandyClients clients;

    public MapController(MapFragment mapFrag) {
        if (mapFrag == null) { throw new IllegalStateException("MapFragment is null"); }
        mMapFragment = mapFrag;
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.obj instanceof Global.WaypointResults) {
            return drawWaypoints((Global.WaypointResults) message.obj);
        }
        return false;
    }

    public void routeSelected(Route route) {
        Message.obtain(clients.vandyVans(), 0,
                       new Global.FetchWaypoints(bridge, route))
                .sendToTarget();
    }

    public boolean drawWaypoints(Global.WaypointResults result) {

        GoogleMap map = mMapFragment.getMap();
        if (map == null) { return true; }

        map.clear();
        map.animateCamera(CameraUpdateFactory.newLatLng(
                new LatLng(Global.DEFAULT_LATITUDE,
                           Global.DEFAULT_LONGITUDE)));



        return true;
    }

}
