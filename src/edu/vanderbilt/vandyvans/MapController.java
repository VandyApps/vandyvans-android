package edu.vanderbilt.vandyvans;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptionsCreator;
import edu.vanderbilt.vandyvans.models.Route;
import edu.vanderbilt.vandyvans.models.Routes;
import edu.vanderbilt.vandyvans.services.Global;
import edu.vanderbilt.vandyvans.services.VandyClients;

/**
 *
 * Created by athran on 3/19/14.
 */
public class MapController implements Handler.Callback,
                                      View.OnClickListener {

    final SupportMapFragment mMapFragment;
    final Handler            bridge = new Handler(this);
    final VandyClients       clients;

    private LinearLayout mOverlayBar;
    private Button       mBlueButton;
    private Button       mRedButton;
    private Button       mGreenButton;

    public MapController(SupportMapFragment mapFrag,
                         LinearLayout       overlayBar,
                         Button             blueBtn,
                         Button             redBtn,
                         Button             greenBtn,
                         VandyClients _clients) {
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
        if (message.obj instanceof Global.WaypointResults) {
            return drawWaypoints((Global.WaypointResults) message.obj);
        }
        return false;
    }

    public void routeSelected(Route route) {
        if (clients == null) {
            throw new IllegalStateException("VandyClient is null");
        }
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
