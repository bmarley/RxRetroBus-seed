package com.blarley.rxretrobusseed.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blarley.rxretrobusseed.App;
import com.blarley.rxretrobus.RetroSubscriber;
import com.blarley.rxretrobus.RetroSubscriberReceiver;
import com.blarley.rxretrobusseed.models.ExampleGetModel;

import java.util.ArrayList;
import java.util.List;

import blake.rxretrobusseed.R;

public class Activity2 extends BoomSubscriber {

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button destroyActivityButton;

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;

    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;
    private ProgressBar progressBar4;
    private ProgressBar progressBar5;
    private ProgressBar progressBar6;

    RetroSubscriber<ExampleGetModel> uncachedRequestSticky = new RetroSubscriber<ExampleGetModel>("getUncachedRequestSticky") {
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

    RetroSubscriber<ExampleGetModel> uncachedRequestNonSticky = new RetroSubscriber<ExampleGetModel>("getUncachedRequestNonSticky") {
        @Override
        public void onLoading() {
            textView5.setText("");
            progressBar5.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(ExampleGetModel response) {
            progressBar5.setVisibility(View.INVISIBLE);
            textView5.setText(response.getExampleField());
        }

        @Override
        public void onError(Throwable throwable) {
            progressBar5.setVisibility(View.INVISIBLE);
            textView5.setText(throwable.getMessage());
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

    RetroSubscriber<ExampleGetModel> uncachedRequestDebouncedSticky = new RetroSubscriber<ExampleGetModel>("getUncachedRequestDebounceSticky") {
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

    RetroSubscriber<ExampleGetModel> uncachedRequestDebouncedNonSticky = new RetroSubscriber<ExampleGetModel>("getUncachedRequestDebounceNonSticky") {
        @Override
        public void onLoading() {
            textView6.setText("");
            progressBar6.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(ExampleGetModel response) {
            progressBar6.setVisibility(View.INVISIBLE);
            textView6.setText(response.getExampleField());
        }

        @Override
        public void onError(Throwable throwable) {
            progressBar6.setVisibility(View.INVISIBLE);
            textView6.setText(throwable.getMessage());
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
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        destroyActivityButton = (Button) findViewById(R.id.destroy_activity_button);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);
        progressBar4 = (ProgressBar) findViewById(R.id.progressBar4);
        progressBar5 = (ProgressBar) findViewById(R.id.progressBar5);
        progressBar6 = (ProgressBar) findViewById(R.id.progressBar6);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.clients.ExampleGet.getUncachedRequestSticky();
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
                App.clients.ExampleGetDebounce.getUncachedRequestSticky();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.clients.ExampleGetDebounce.getCachedRequest("example-get");
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.clients.ExampleGet.getUncachedRequestNonSticky();
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.clients.ExampleGetDebounce.getUncachedRequestNonSticky();
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
        App.bus.register(this);
    }

    @Override
    public List<RetroSubscriber> getSubscribers() {
        List<RetroSubscriber> subs = super.getSubscribers();
        subs.add(cachedRequest);
        subs.add(uncachedRequestSticky);
        subs.add(uncachedRequestNonSticky);
        subs.add(uncachedRequestDebouncedSticky);
        subs.add(uncachedRequestDebouncedNonSticky);
        subs.add(cachedRequestDebounced);
        return subs;
    }
}
