package edu.vanderbilt.vandyvans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import edu.vanderbilt.vandyvans.models.ArrivalTime;
import edu.vanderbilt.vandyvans.models.Routes;
import edu.vanderbilt.vandyvans.models.Stop;
import edu.vanderbilt.vandyvans.models.Stops;
import edu.vanderbilt.vandyvans.services.Global;

public final class DetailActivity extends Activity implements Handler.Callback {

    private static final String TAG_ID = "stopId";

    private ArrivalTimeViewHolder mBlueGroup;
    private ArrivalTimeViewHolder mRedGroup;
    private ArrivalTimeViewHolder mGreenGroup;
    private ProgressBar mArrivalLoading;
    private TextView mFailureText;

    private Handler controller;
    private Stop stop;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_stopdetail);

        getViewReferences();
        mBlueGroup.hide();
        mRedGroup.hide();
        mGreenGroup.hide();
        mFailureText.setVisibility(View.GONE);

        // Setup a Handler to receive replies from the services.
        controller = new Handler(this);

        final int stopId = getIntent().getIntExtra(TAG_ID, 0);
        stop = Stops.getForId(stopId);
        getActionBar().setTitle(stop.name);

        if (stopId == 0) {
            throw new IllegalStateException("No Stop to be detailed. Why do you even call me?");

        } else {
            Global.syncromaticsClient()
                    .obtainMessage(0,
                            new Global.FetchArrivalTimes(
                                    controller,
                                    stop))
                    .sendToTarget();

        }

    }

    private void getViewReferences() {
        mBlueGroup = new ArrivalTimeViewHolder(
                findViewById(R.id.rl1),
                findViewById(R.id.tv1));

        mRedGroup = new ArrivalTimeViewHolder(
                findViewById(R.id.rl2),
                findViewById(R.id.tv2));

        mGreenGroup = new ArrivalTimeViewHolder(
                findViewById(R.id.rl3),
                findViewById(R.id.tv3));

        mArrivalLoading = (ProgressBar) findViewById(R.id.progress1);
        mFailureText = (TextView) findViewById(R.id.tv4);
    }


    public static void openForId(int id, Context ctx) {
        Intent i = new Intent(ctx, DetailActivity.class);
        i.putExtra(TAG_ID, id);
        ctx.startActivity(i);
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.obj instanceof Global.ArrivalTimeResults) {
            return displayArrivalTimes(((Global.ArrivalTimeResults) message.obj).times);
        }
        return false;
    }

    private boolean displayArrivalTimes(List<ArrivalTime> times) {

        mArrivalLoading.setVisibility(View.GONE);

        if (times.isEmpty()) {
            mFailureText.setVisibility(View.VISIBLE);

        } else {
            for (ArrivalTime time : times) {
                if (time.route == Routes.BLUE) {
                    mBlueGroup.displayTime(time.minutes);
                    mBlueGroup.show();

                } else if (time.route == Routes.GREEN) {
                    mGreenGroup.displayTime(time.minutes);
                    mGreenGroup.show();

                } else if (time.route == Routes.RED) {
                    mRedGroup.displayTime(time.minutes);
                    mRedGroup.show();
                }
            }
        }

        return true;
    }

    private static class ArrivalTimeViewHolder {

        final View view;
        final TextView timeDisplay;

        ArrivalTimeViewHolder(View v, View disp) {
            view = v;
            timeDisplay = (TextView) disp;
        }

        void displayTime(int minutes) {
            timeDisplay.setText(minutes + " minutes");
        }

        void hide() {
            view.setVisibility(View.GONE);
        }

        void show() {
            view.setVisibility(View.VISIBLE);
        }

    }

}
