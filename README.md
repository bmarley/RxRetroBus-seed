This is the seed project that will serve as an example for all of the functionality within the [RxRetroBus](https://github.com/bmarley/RxRetroBus) library.

RxRetroBus
-------

The aim of RxRetroBus is to tie together [Square's Retrofit](http://square.github.io/retrofit/) and 
[ReactiveX's RxJava](https://github.com/ReactiveX/RxJava) with simple annotations to eliminate 
boilerplate and give the programmer an event based Bus that will manage the state of an application.

By annotating a class with ```@GenerateEvents```, RxRetroBus' annotation processor will generate 
files tying together your defined Events and the Bus, allowing you to publish/subscribe to the event's tag 
in your Activities and Fragments.

## How to Use
To include in your project:

***0.5.1 - Experimental***

Add these two dependencies into your build.gradle file. 
```
    annotationProcessor 'com.blarley.rxretrobus:rxretrobus-processor:0.5.1'
    compile 'com.blarley.rxretrobus:rxretrobus:0.5.1@aar'
```

**Ideally you'd only need RxRetroBus and it's annotation processor as dependencies; however, for now you'll still need to include Retrofit in your base project - I'll remove the reliance on it 
and RxJava in later versions.**

However, ***for now*** also add these to your dependencies:
```
    compile 'com.squareup.retrofit2:retrofit:2.2.0'
    compile 'com.squareup.retrofit2:converter-jackson:2.2.0'
    compile 'com.squareup.retrofit2:adapter-rxjava2:2.2.0'
    compile 'io.reactivex.rxjava2:rxjava:2.0.4'
```

### Creating the Bus
```java
    private Retrofit.Builder retrofitBuilder;
    OkHttpClient okHttpClient = new OkHttpClient()
            .newBuilder()
            .build();

    Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient);
    RxRetroBus bus; = new RxRetroBus();
    Clients clients = new Clients(retrofitBuilder, bus);
```
You'll need to either expose the clients and bus as static variables on the global App Object or use 
Dagger/Another DI injection framework to wire them into your classes.

### Annotations and Their Uses
#### GenerateEvents
Annotate Retrofit interfaces with this annotation, supply the baseUrl of the given API in the arguments. 
As the name suggests, the baseUrl will be used as the base URL when building the retrofit client.

```java
@GenerateEvents(baseUrl = "http://api.blarley.com/")
public interface ExampleGet {
   ...

```

The events in this library can also be used with concrete classes. So long as the method that you're 
writing returns an Observable, any event annotation can be used. This is great for events that you 
would still like to manage the state of or would like to pub/sub with, even though they aren't API calls.


```java
@GenerateEvents(retrofit = false)
public class ClickEvent {

    @UncachedEvent(tag = "setUpBomb", debounce = true, sticky = true)
    public Observable<ExampleGetModel> setUpBomb() {
        return Observable.just(new ExampleGetModel()).delay(5, TimeUnit.SECONDS);
    }
}
```

#### Events Annotations
Individual methods within either Retrofit interfaces or concrete classes must be annotated in order to be 
registered with the bus.

#### CachedEvent
This event will remain cached by the Bus, until it's overridden by another response with the same tag 
or cleared**. 

If anything subscribes to the Bus and there is a cached value for this specific event, it will immediately 
invoke the RetroSubscriber that was used to subscribe.

**Functionality doesn't exist yet.

You must specify a `tag`.  
Optionally you can specify whether you want to `debounce` the event. Defaults to `false`. Debouncing in this sense means that 
if the event fires, you cannot trigger it again until it completes.

```java
    @GET("example-get")
    @CachedEvent(tag = "getCachedRequest")
    Observable<ExampleGetModel> getCachedRequest();
    
    @GET("example-get")
    @CachedEvent(tag = "getCachedRequestDebounce", debounce = true)
    Observable<ExampleGetModel> getCachedRequest(@Path("s") String s);
    
```

Again, it's worth noting that you could remove the retrofit annotations, put this in a concrete class, 
and define the observable yourself.

#### UncachedEvent
This event will not be cached by the Bus.
Optionally you can supply the `sticky` flag to this annotation. Defaults to `false`; however, if set to `true` 
it will cache the response until an Activity or Fragment subscribes that event. Once done, it will be 
consumed and removed from the cache.
Optionally you can also specify whether you want to `debounce` the event.

```java
    @GET("example-get")
    @UncachedEvent(tag = "getUncachedRequestSticky", sticky = true)
    Observable<ExampleGetModel> getUncachedRequestSticky();

    @GET("example-get")
    @UncachedEvent(tag = "getUncachedRequestNonSticky")
    Observable<ExampleGetModel> getUncachedRequestNonSticky();
    
    @GET("example-get")
    @UncachedEvent(tag = "getUncachedRequestDebounceSticky", debounce = true, sticky = true)
    Observable<ExampleGetModel> getUncachedRequestSticky();

    @GET("example-get")
    @UncachedEvent(tag = "getUncachedRequestDebounceNonSticky", debounce = true)
    Observable<ExampleGetModel> getUncachedRequestNonSticky();
```

#### FireAndForgetEvent
As the name suggests, this event will fire and publish to anything that is currently subscribed to this event.
It is functionally equivalent to an UncachedEvent with it's sticky flag set to false. The difference is 
this event can never be made sticky.

You must specify a `tag`.  
Optionally you can also specify whether you want to `debounce` the event.
```java
    @GET("example-get")
    @FireAndForgetEvent(tag = "getFireAndForget")
    Observable<ExampleGetModel> getFireAndForget();

    @GET("example-get")
    @FireAndForgetEvent(tag = "getFireAndForgetDebounced", debounce = true)
    Observable<ExampleGetModel> getFireAndForgetDebounced();
```

### Publishing
Publishing is as simple as retrieving the clients object, accessing the client, and calling the method on that client.

Using the ClickEvent example above.. The generated `Clients` object will contain public variables that 
match the names of your classes that you have annotated with `GenerateEvents`.  
The method names on that object will match what you defined in your Retrofit interfaces/concrete classes.
```
    clients.ClickEvent.setUpBomb()
```

This will subscribe to the Observable that's returned. This will cause a loading event to be fired to 
anything subscribed to that tag. When the Observable completes, another event will be published to any 
RetroSubscribers.

### Subscribing
Currently, any class that will subscribe to an RxRetroBus will need to `implements RetroSubscriberReceiver`.  
This interface will force the class to implement `List<RetroSubscriber> getSubscribers();`.  
As this method suggests, it must return a list of `RetroSubscriber` objects. This is the method the bus calls to register subscribers.

These look like this:
```java
RetroSubscriber<ExampleGetModel> catchBoom = new RetroSubscriber<ExampleGetModel>("setUpBomb") {
    @Override
    public void onLoading() {

    }


    @Override
    public void onSuccess(ExampleGetModel response) {
        ...Do Success Stuff
    }


    @Override
    public void onError(Throwable throwable) {
        ...Do Failure Stuff
    }
};
```

Finally, the class must register with the bus in the `OnPause` and `OnResume` methods.
```java
@Override
protected void onPause() {
    super.onPause();
    App.bus.unregister(this);
}

@Override
protected void onResume() {
    super.onResume();
    App.bus.register(this);
}
```

License
-------

    Copyright 2017 Blake Marley

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
