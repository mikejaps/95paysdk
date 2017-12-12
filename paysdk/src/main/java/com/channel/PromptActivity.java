package com.channel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.gandalf.a.R;

/**
 * Created by as on 17-8-11.
 */

public class PromptActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }


    private void initView() {
        setContentView(R.layout.activity_prompt);
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(getIntent().getStringExtra("msg"));
    }

}
