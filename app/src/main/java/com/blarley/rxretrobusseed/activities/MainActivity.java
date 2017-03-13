package com.blarley.rxretrobusseed.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blarley.rxretrobusseed.App;
import com.blarley.rxretrobusseed.models.ExampleGetModel;

import blake.rxretrobusseed.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.clients.ExampleGet.exampleGet();
    }
}
