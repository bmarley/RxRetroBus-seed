package com.blarley.rxretrobusseed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blarley.rxretrobusseed.App;

import blake.rxretrobusseed.R;

public class Activity1 extends BoomSubscriber {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);
        findViewById(R.id.start_activity_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity1.this, Activity2.class));
                overridePendingTransition(0, 0);
            }
        });
        findViewById(R.id.setup_bomb_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.clients.ClickEvent.setUpBomb();
            }
        });
    }
}
