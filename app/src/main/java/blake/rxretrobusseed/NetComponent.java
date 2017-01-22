package blake.rxretrobusseed;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by Blake on 1/21/17.
 */
@Singleton
@Component(modules= { NetModule.class })
public interface NetComponent {
    Retrofit provideRetrofitBuilder();
}
