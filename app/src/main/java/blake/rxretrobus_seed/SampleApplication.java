package blake.rxretrobus_seed;

import android.app.Application;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("yolo.com").addCallAdapterFactory(AdapterFactory.)
        OkHttpClient httpClient = new OkHttpClient().Builder()
                .a(JacksonConverterFactory.create())
                .build();
    }
}
