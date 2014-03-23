package edu.vanderbilt.vandyvans;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import edu.vanderbilt.vandyvans.models.Routes;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public final class RouteMapFragment
        extends RoboFragment
        implements View.OnClickListener {

    @InjectView(R.id.linear1)   LinearLayout mBar;
    @InjectView(R.id.btn_blue)  Button       mBlueButton;
    @InjectView(R.id.btn_red)   Button       mRedButton;
    @InjectView(R.id.btn_green) Button       mGreenButton;

    MapController mController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBlueButton.setOnClickListener(this);
        mRedButton.setOnClickListener(this);
        mGreenButton.setOnClickListener(this);
    }

    public void setController(MapController cont) {
        mController = cont;
    }

    @Override
    public void onClick(View view) {
        if (mController == null) { return; }
        if (view == mBlueButton) {
            mBar.setBackgroundColor(getResources().getColor(R.color.blue));
            mController.routeSelected(Routes.BLUE);
        } else if (view == mRedButton) {
            mBar.setBackgroundColor(getResources().getColor(R.color.red));
            mController.routeSelected(Routes.RED);
        } else if (view == mGreenButton) {
            mBar.setBackgroundColor(getResources().getColor(R.color.green));
            mController.routeSelected(Routes.GREEN);
        }
    }
}
