package com.blarley.rxretrobusseed.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blarley.rxretrobusseed.App;
import com.blarley.rxretrobusseed.library.bus.RetroSubscriber;
import com.blarley.rxretrobusseed.models.ExampleGetModel;

import java.util.ArrayList;
import java.util.List;

import blake.rxretrobusseed.R;

public class Activity2 extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button destroyActivityButton;

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;

    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;
    private ProgressBar progressBar4;


    RetroSubscriber<ExampleGetModel> uncachedRequest = new RetroSubscriber<ExampleGetModel>("getUncachedRequest") {
        @Override
        public void onLoading() {
            textView1.setText("");
            progressBar1.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(ExampleGetModel response) {
            progressBar1.setVisibility(View.INVISIBLE);
            textView1.setText(response.getExampleField());
        }

        @Override
        public void onError(Throwable throwable) {
            progressBar1.setVisibility(View.INVISIBLE);
            textView1.setText(throwable.getMessage());
        }
    };

    RetroSubscriber<ExampleGetModel> cachedRequest = new RetroSubscriber<ExampleGetModel>("getCachedRequest") {
        @Override
        public void onLoading() {
            textView2.setText("");
            progressBar2.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(ExampleGetModel response) {
            progressBar2.setVisibility(View.INVISIBLE);
            textView2.setText(response.getExampleField());
        }

        @Override
        public void onError(Throwable throwable) {
            progressBar2.setVisibility(View.INVISIBLE);
            textView2.setText(throwable.getMessage());
        }
    };

    RetroSubscriber<ExampleGetModel> uncachedRequestDebounced = new RetroSubscriber<ExampleGetModel>("getUncachedRequestDebounce") {
        @Override
        public void onLoading() {
            textView3.setText("");
            progressBar3.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(ExampleGetModel response) {
            progressBar3.setVisibility(View.INVISIBLE);
            textView3.setText(response.getExampleField());
        }

        @Override
        public void onError(Throwable throwable) {
            progressBar3.setVisibility(View.INVISIBLE);
            textView3.setText(throwable.getMessage());
        }
    };

    RetroSubscriber<ExampleGetModel> cachedRequestDebounced = new RetroSubscriber<ExampleGetModel>("getCachedRequestDebounce") {
        @Override
        public void onLoading() {
            textView4.setText("");
            progressBar4.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(ExampleGetModel response) {
            progressBar4.setVisibility(View.INVISIBLE);
            textView4.setText(response.getExampleField());
        }

        @Override
        public void onError(Throwable throwable) {
            progressBar4.setVisibility(View.INVISIBLE);
            textView4.setText(throwable.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        destroyActivityButton = (Button) findViewById(R.id.destroy_activity_button);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);
        progressBar4 = (ProgressBar) findViewById(R.id.progressBar4);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.clients.ExampleGet.getUncachedRequest();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.clients.ExampleGet.getCachedRequest();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.clients.ExampleGetDebounce.getUncachedRequest();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.clients.ExampleGetDebounce.getCachedRequest("example-get");
            }
        });

        destroyActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
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
        subs.add(cachedRequest);
        subs.add(uncachedRequest);
        subs.add(uncachedRequestDebounced);
        subs.add(cachedRequestDebounced);
        App.bus.register(this, subs);
    }
}
