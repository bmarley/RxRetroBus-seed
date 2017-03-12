package com.blarley.rxretrobusseed;

import android.app.Application;

import com.blarley.rxretrobusseed.api.Clients;
import com.blarley.rxretrobusseed.util.ClientsComponent;
import com.blarley.rxretrobusseed.util.ClientsModule;
import com.blarley.rxretrobusseed.util.DaggerClientsComponent;
import com.blarley.rxretrobusseed.util.DaggerNetComponent;
import com.blarley.rxretrobusseed.util.NetComponent;
import com.blarley.rxretrobusseed.util.NetModule;

public class App extends Application {
    private NetComponent mNetComponent;
    private ClientsComponent mClientsComponent;
    public static Clients clients;

    @Override
    public void onCreate() {
        super.onCreate();

        mNetComponent = DaggerNetComponent.builder()
                .netModule(new NetModule())
                .build();

        mClientsComponent = DaggerClientsComponent.builder()
                .clientsModule(new ClientsModule(mNetComponent))
                .build();

        clients = new Clients(this);
    }

    public ClientsComponent getClientsComponent() {
        return mClientsComponent;
    }
}
