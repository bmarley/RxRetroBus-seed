package blake.rxretrobusseed;

import blake.rxretrobusseed.annotationprocessor.processor.GenerateEvents;
import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by Blake on 1/21/17.
 */

@GenerateEvents
public interface ExampleGet {
    @GET("example-get")
    Observable<ExampleGetModel> exampleGet();
}
