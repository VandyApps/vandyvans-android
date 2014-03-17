package edu.vanderbilt.vandyvans;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.vanderbilt.vandyvans.models.Report;
import edu.vanderbilt.vandyvans.services.Global;


/**
 * Created by athran on 3/17/14.
 */
public final class FormActivity extends Activity {

    static final String TAG_FORMTITLE = "form_title";
    static final String TAG_FORMBODYHINT = "form_body_hint";
    static final String RESULT_EMAIL = "result_email";
    static final String RESULT_BODY = "result_body";

    private TextView mFormTitle;
    private EditText mEmailField;
    private EditText mBodyField;
    private Button mSubmit;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.report_form);
        getViewReferences();

        final FormConfig conf = readConfig(getIntent().getExtras());
        mFormTitle.setText(conf.formTitle);
        mBodyField.setHint(conf.bodyHint);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmailField.getText().toString();
                final String body = mBodyField.getText().toString();

                if (email == null || email.equals("")) {
                    showMessage("Please fill in your E-mail address");

                } else if (body == null || body.equals("")) {
                    showMessage("Please fill in the description");

                } else {
                    setResult(
                            Activity.RESULT_OK,
                            getIntent()
                                    .putExtra(RESULT_EMAIL, email)
                                    .putExtra(RESULT_BODY, body));
                    FormActivity.this.finish();
                }
            }
        });
    }

    private void getViewReferences() {
        mFormTitle  = (TextView) findViewById(R.id.textView1);
        mEmailField = (EditText) findViewById(R.id.editText);
        mBodyField  = (EditText) findViewById(R.id.editText2);
        mSubmit     = (Button)   findViewById(R.id.button3);
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
            bodyHint = _hint;
        }
    }

}
