package blake.rxretrobusseed;

import android.app.Application;

public class SampleApplication extends Application {
    private NetComponent mNetComponent;
    private ClientsComponent mClientsComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mNetComponent = DaggerNetComponent.builder()
                .netModule(new NetModule("https://api.github.com"))
                .build();

        mClientsComponent = DaggerClientsComponent.builder()
                .netComponent(mNetComponent)
                .clientsModule(new ClientsModule())
                .build();

        new Testing(this);
    }

    public ClientsComponent getClientsComponent() {
        return mClientsComponent;
    }
}
