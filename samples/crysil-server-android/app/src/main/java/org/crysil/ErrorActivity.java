package org.crysil;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Joiner;

import java.io.Serializable;

public class ErrorActivity extends AbstractActivity {

    public static final String REASON = "REASON";
    public static final String MSG = "MSG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        if (getIntent().hasExtra(REASON)) {
            Serializable error = getIntent().getSerializableExtra(REASON);
            if (error instanceof Exception) {
                TextView textView = (TextView) findViewById(R.id.tvErrorReason);
                textView.setText(((Exception) error).getMessage() + "\n");
                textView.append(Joiner.on("\n").join(((Exception) error).getStackTrace()));
            }
        }
        if (getIntent().hasExtra(MSG)) {
            TextView textView = (TextView) findViewById(R.id.tvErrorMessage);
            textView.setText(getIntent().getSerializableExtra(MSG).toString());
        }
    }

    public void btExitOnClick(View view) {
        finish();
    }
}
