package edu.vanderbilt.vandyvans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import edu.vanderbilt.vandyvans.models.Report;
import edu.vanderbilt.vandyvans.services.Global;

public final class AboutsActivity extends Activity {

    private static final String TAG_FORMTYPE = "formtype";
    private static final int TAG_BUG = 1000;
    private static final int TAG_FEED = 1111;

    private Button mBugReport;
    private Button mFeedbackReport;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_about);
        getViewReferences();

        mBugReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AboutsActivity.this, FormActivity.class);
                i.putExtra(TAG_FORMTYPE, TAG_BUG);
                i.putExtra(FormActivity.TAG_FORMTITLE, "Report a Bug");
                i.putExtra(FormActivity.TAG_FORMBODYHINT, "describe the bug");
                startActivityForResult(i, -1);
            }
        });

        mFeedbackReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AboutsActivity.this, FormActivity.class);
                i.putExtra(TAG_FORMTYPE, TAG_FEED);
                i.putExtra(FormActivity.TAG_FORMTITLE, "Send Feedback");
                i.putExtra(FormActivity.TAG_FORMBODYHINT, "thoughts on the app");
                startActivityForResult(i, -1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        Global.vandyVansClient()
                .obtainMessage(
                        0,
                        new Report(
                                (data.getIntExtra(TAG_FORMTYPE, TAG_FEED) == TAG_BUG),
                                data.getStringExtra(FormActivity.RESULT_EMAIL),
                                data.getStringExtra(FormActivity.RESULT_BODY),
                                false))
                .sendToTarget();
    }

    private void getViewReferences() {
        mBugReport = (Button) findViewById(R.id.button1);
        mFeedbackReport = (Button) findViewById(R.id.button2);
    }
    
    public static void open(Context ctx) {
        Intent i = new Intent(ctx, AboutsActivity.class);
        ctx.startActivity(i);
    }


}
