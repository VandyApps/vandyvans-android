package edu.vanderbilt.vandyvans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public final class DetailActivity extends Activity {

    private static final String TAG_ID = "stopId";

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_stopdetail);
    }



    public static void openForId(int id, Context ctx) {
        Intent i = new Intent(ctx, DetailActivity.class);
        i.putExtra(TAG_ID, id);
        ctx.startActivity(i);
    }
}
