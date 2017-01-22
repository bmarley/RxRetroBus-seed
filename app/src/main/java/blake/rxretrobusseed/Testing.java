package blake.rxretrobusseed;

import android.app.Application;

import javax.inject.Inject;

/**
 * Created by Blake on 1/21/17.
 */
public class Testing {
    @Inject ExampleGet exampleGet;
    public Testing(Application application) {
        ((SampleApplication) application).getClientsComponent().inject(this);
    }
}
