package edu.vanderbilt.vandyvans;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * The form for submitting feedbacks and bugreports.
 *
 * Created by athran on 3/17/14.
 */
public final class FormActivity extends RoboActivity {

    static final String TAG_FORMTITLE    = "form_title";
    static final String TAG_FORMBODYHINT = "form_body_hint";
    static final String RESULT_EMAIL     = "result_email";
    static final String RESULT_BODY      = "result_body";
    static final int    RESULT_EXIST     = 9090;

    @InjectView(R.id.textView1) private TextView mFormTitle;
    @InjectView(R.id.editText)  private EditText mEmailField;
    @InjectView(R.id.editText2) private EditText mBodyField;
    @InjectView(R.id.button3)   private Button   mSubmit;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.report_form);

        final FormConfig conf = readConfig(getIntent().getExtras());
        mFormTitle.setText(conf.formTitle);
        mBodyField.setHint(conf.bodyHint);

        // Set the default result code. When user press the back button, this
        // code will be returned by the calling activity. When the submit
        // button is pressed, the proper result code will be set.
        setResult(RESULT_CANCELED);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmailField.getText().toString();
                final String body  = mBodyField.getText().toString();

                if (email == null || email.equals("")) {
                    showMessage("Please fill in your E-mail address");

                } else if (body == null || body.equals("")) {
                    showMessage("Please fill in the description");

                } else {
                    setResult(
                            RESULT_EXIST,
                            getIntent()
                                    .putExtra(RESULT_EMAIL, email)
                                    .putExtra(RESULT_BODY, body));
                    FormActivity.this.finish();
                }
            }
        });
    }

    private FormConfig readConfig(Bundle args) {
        return new FormConfig(
                args.getString(TAG_FORMTITLE),
                args.getString(TAG_FORMBODYHINT));
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static final class FormConfig {
        final String formTitle;
        final String bodyHint;

        FormConfig(String _title, String _hint) {
            formTitle = _title;
            bodyHint  = _hint;
        }
    }

}
