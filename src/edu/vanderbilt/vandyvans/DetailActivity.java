package edu.vanderbilt.vandyvans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public final class DetailActivity extends Activity {

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_stopdetail);
    }
    
    public static void open(Context ctx) {
        Intent i = new Intent(ctx, DetailActivity.class);
        ctx.startActivity(i);
    }
}
