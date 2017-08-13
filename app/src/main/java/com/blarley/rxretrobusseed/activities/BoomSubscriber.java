package com.blarley.rxretrobusseed.activities;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.blarley.rxretrobus.RetroSubscriber;
import com.blarley.rxretrobus.RetroSubscriberReceiver;
import com.blarley.rxretrobusseed.models.ExampleGetModel;

import java.util.ArrayList;
import java.util.List;


public class BoomSubscriber extends AppCompatActivity implements RetroSubscriberReceiver {
    RetroSubscriber<ExampleGetModel> catchBoom = new RetroSubscriber<ExampleGetModel>("setUpBomb") {
        @Override
        public void onLoading() {

        }


        @Override
        public void onSuccess(ExampleGetModel response) {
            Toast.makeText(BoomSubscriber.this, "BOOM", Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onError(Throwable throwable) {
            Toast.makeText(BoomSubscriber.this, "Something went wrong with the bomb...", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public List<RetroSubscriber> getSubscribers() {
        List<RetroSubscriber> subs = new ArrayList<>();
        subs.add(catchBoom);
        return subs;
    }
}
