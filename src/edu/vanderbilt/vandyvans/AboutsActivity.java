package edu.vanderbilt.vandyvans;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.inject.Inject;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import edu.vanderbilt.vandyvans.models.Report;
import edu.vanderbilt.vandyvans.services.VandyClients;


public final class AboutsActivity extends RoboActivity {

    private static final String TAG_FORMTYPE = "formtype";
    private static final int    TAG_BUG      = 1000;
    private static final int    TAG_FEED     = 1111;

    @InjectView(R.id.button1) private Button mBugReport;
    @InjectView(R.id.button2) private Button mFeedbackReport;

    @Inject VandyClients clients;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_about);

        if (clients == null) throw new IllegalStateException("Vandy Clients is null");

        mBugReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent i = new Intent(AboutsActivity.this,
                                            FormActivity.class);
                i.putExtra(TAG_FORMTYPE                 , TAG_BUG);
                i.putExtra(FormActivity.TAG_FORMTITLE   , "Report a Bug");
                i.putExtra(FormActivity.TAG_FORMBODYHINT, "describe the bug");
                startActivityForResult(i, 1);
            }
        });

        mFeedbackReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent i = new Intent(AboutsActivity.this,
                                            FormActivity.class);
                i.putExtra(TAG_FORMTYPE                 , TAG_FEED);
                i.putExtra(FormActivity.TAG_FORMTITLE   , "Send Feedback");
                i.putExtra(FormActivity.TAG_FORMBODYHINT, "thoughts on the app");
                startActivityForResult(i, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int    requestCode,
                                    int    resultCode,
                                    Intent data) {

        if (resultCode == RESULT_CANCELED) { return; }

        clients.vandyVans()
                .obtainMessage(
                        0,
                        new Report(
                                (data.getIntExtra(TAG_FORMTYPE, TAG_FEED) == TAG_BUG),
                                data.getStringExtra(FormActivity.RESULT_EMAIL),
                                data.getStringExtra(FormActivity.RESULT_BODY),
                                false))
                .sendToTarget();
    }
    
    public static void open(Context ctx) {
        Intent i = new Intent(ctx, AboutsActivity.class);
        ctx.startActivity(i);
    }



}
