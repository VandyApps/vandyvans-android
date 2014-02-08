package edu.vanderbilt.vandyvans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AboutsActivity extends Activity {

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_about);
    }
    
    public static final void open(Context ctx) {
        Intent i = new Intent(ctx, AboutsActivity.class);
        ctx.startActivity(i);
    }
    
}
