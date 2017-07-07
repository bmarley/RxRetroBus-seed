package com.blarley.rxretrobusseed.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import blake.rxretrobusseed.R;

public class Activity1 extends AppCompatActivity {

    private Button button;
    Context self = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(self, Activity2.class));
                overridePendingTransition(0, 0);
            }
        });
    }
}
