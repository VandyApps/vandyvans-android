package edu.vanderbilt.vandyvans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public final class DetailActivity extends Activity {

    private static final String TAG_ID = "stopId";

    private ArrivalTimeViewHolder mBlueGroup;
    private ArrivalTimeViewHolder mRedGroup;
    private ArrivalTimeViewHolder mGreenGroup;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_stopdetail);

        mBlueGroup  = new ArrivalTimeViewHolder(
                findViewById(R.id.rl1), findViewById(R.id.tv1));
        mRedGroup   = new ArrivalTimeViewHolder(
                findViewById(R.id.rl2), findViewById(R.id.tv2));
        mGreenGroup = new ArrivalTimeViewHolder(
                findViewById(R.id.rl3), findViewById(R.id.tv3));

        int stopId = saved.getInt(TAG_ID);
        if (stopId == 0) {
            throw new IllegalStateException("No Stop to be detailed. Why do you even call me?");
        } else {

        }

    }



    public static void openForId(int id, Context ctx) {
        Intent i = new Intent(ctx, DetailActivity.class);
        i.putExtra(TAG_ID, id);
        ctx.startActivity(i);
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
