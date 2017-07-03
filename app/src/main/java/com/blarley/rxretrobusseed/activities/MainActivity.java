package com.blarley.rxretrobusseed.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blarley.rxretrobusseed.library.bus.RetroSubscriber;
import com.blarley.rxretrobusseed.App;
import com.blarley.rxretrobusseed.models.ExampleGetModel;

import java.util.ArrayList;
import java.util.List;

import blake.rxretrobusseed.R;

public class MainActivity extends AppCompatActivity {

    RetroSubscriber<ExampleGetModel> retroSubscriber = new RetroSubscriber<ExampleGetModel>("test") {
        @Override
        public void onLoading() {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(ExampleGetModel response) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Success: " + response.getExampleField(), Toast.LENGTH_LONG).show();
            Log.d("test", response.toString());
        }

        @Override
        public void onError(Throwable throwable) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("test", throwable.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.clients.ExampleGet.exampleGet();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.bus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<RetroSubscriber> subs = new ArrayList<>();
        subs.add(retroSubscriber);
        App.bus.register(this, subs);
    }
}
