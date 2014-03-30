package edu.vanderbilt.vandyvans;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import com.google.inject.Inject;
import edu.vanderbilt.vandyvans.models.ArrivalTime;
import edu.vanderbilt.vandyvans.models.Routes;
import edu.vanderbilt.vandyvans.models.Stop;
import edu.vanderbilt.vandyvans.models.Stops;
import edu.vanderbilt.vandyvans.services.Global;
import edu.vanderbilt.vandyvans.services.VandyClients;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public final class DetailActivity extends RoboActivity implements Handler.Callback {

    private static final String TAG_ID = "stopId";

    private ArrivalTimeViewHolder mBlueGroup;
    private ArrivalTimeViewHolder mRedGroup;
    private ArrivalTimeViewHolder mGreenGroup;

    @InjectView(R.id.rl1) private RelativeLayout mBlueRL;
    @InjectView(R.id.tv1) private TextView       mBlueDisp;
    @InjectView(R.id.rl2) private RelativeLayout mRedRL;
    @InjectView(R.id.tv2) private TextView       mRedDisp;
    @InjectView(R.id.rl3) private RelativeLayout mGreenRL;
    @InjectView(R.id.tv3) private TextView       mGreenDisp;

    @InjectView(R.id.progress1) private ProgressBar mArrivalLoading;
    @InjectView(R.id.tv4)       private TextView    mFailureText;
    @InjectView(R.id.tv5)       private TextView    mReminderText;
    @InjectView(R.id.cb1)       private Switch      mReminderSwitch;

    private ReminderController reminderController;
    private Handler controller;
    private Stop stop;
    @Inject VandyClients apiClient;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_stopdetail);

        // Setup controllers for a cleaner handling of the Views' state transitions.
        mBlueGroup =  new ArrivalTimeViewHolder(mBlueRL,  mBlueDisp);
        mRedGroup =   new ArrivalTimeViewHolder(mRedRL,   mRedDisp);
        mGreenGroup = new ArrivalTimeViewHolder(mGreenRL, mGreenDisp);

        // Hide everything except the progress bar while we wait for the
        // API call to the VandyVans server.
        mBlueGroup.hide();
        mRedGroup.hide();
        mGreenGroup.hide();
        mFailureText.setVisibility(View.GONE);

        // Setup a Handler to receive replies from the services.
        controller = new Handler(this);

        // Pull the Stop info from the argument.
        final int stopId = getIntent().getIntExtra(TAG_ID, 0);
        if (stopId == 0) {
            throw new IllegalStateException(
                    "No Stop to be detailed. Why do you even call me?");
        }

        stop = Stops.getForId(stopId);
        getActionBar().setTitle(stop.name);

        // Request arrival times from the server.
        Message.obtain(apiClient.syncromatics(), 0,
                       new Global.FetchArrivalTimes(
                               controller,
                               stop))
                .sendToTarget();

        // Setup the controller for the Reminder subsystem.
        reminderController = new ReminderController(mReminderSwitch,
                                                    mReminderText);
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

        // Dismiss the progress bar.
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
            timeDisplay.setText(
                    minutes +
                            ((minutes <= 1) ? " minute" : " minutes"));
        }

        void hide() {
            view.setVisibility(View.GONE);
        }

        void show() {
            view.setVisibility(View.VISIBLE);
        }

    }

    private static class ReminderController
            implements View.OnClickListener,
                       CompoundButton.OnCheckedChangeListener {

        final Switch   mSwitch;
        final TextView mText;

        ReminderController(Switch _switch, TextView _text) {
            mSwitch = _switch;
            mText   = _text;

            mSwitch.setOnCheckedChangeListener(this);
            mText.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSwitch.toggle();
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            // TODO
        }
    }

}
