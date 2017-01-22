package blake.rxretrobusseed;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Blake on 1/21/17.
 */
@Module
public class NetModule {

    String mBaseUrl;
    OkHttpClient mOkHttpClient = new OkHttpClient()
            .newBuilder()
            .build();

    // Constructor needs one parameter to instantiate.
    public NetModule(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofitBuilder() {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(mBaseUrl)
                .client(mOkHttpClient)
                .build();

        return retrofit;
    }
}
