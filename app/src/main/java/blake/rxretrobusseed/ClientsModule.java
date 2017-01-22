package blake.rxretrobusseed;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Blake on 1/21/17.
 */
@Module
public class ClientsModule {

    @Provides
    @ApplicationScope
    public ExampleGet providesExampleGetInterface(Retrofit retrofit) {
        final ExampleGet get =  retrofit.create(ExampleGet.class);

        ExampleGet test = (ExampleGet) Proxy.newProxyInstance(ExampleGet.class.getClassLoader(), new Class<?>[]{ExampleGet.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("Current hack to hijack the observable and add it to the bus - ideally this would be a separate" +
                                "call adapter for retrofit");
                        return get.getClass().getMethod(method.getName()).invoke(get, args);

                    }
                });

        /*
        Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
        new InvocationHandler() {
          private final Platform platform = Platform.get();

          @Override public Object invoke(Object proxy, Method method, Object... args)
              throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
              return method.invoke(this, args);
            }
            if (platform.isDefaultMethod(method)) {
              return platform.invokeDefaultMethod(method, service, proxy, args);
            }
            ServiceMethod serviceMethod = loadServiceMethod(method);
            OkHttpCall okHttpCall = new OkHttpCall<>(serviceMethod, args);
            return serviceMethod.callAdapter.adapt(okHttpCall);
          }
        });
         */
        System.out.println(test.exampleGet());
        return get;
    }

}
