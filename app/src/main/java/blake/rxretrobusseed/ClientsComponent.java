package blake.rxretrobusseed;

import dagger.Component;

/**
 * Created by Blake on 1/21/17.
 */
@ApplicationScope
@Component(dependencies = NetComponent.class, modules = ClientsModule.class)
public interface ClientsComponent {
    void inject(Testing testing);
}