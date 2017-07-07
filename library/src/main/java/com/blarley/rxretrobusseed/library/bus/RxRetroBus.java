package com.blarley.rxretrobusseed.library.bus;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Blake on 3/12/17.
 */

public class RxRetroBus {

    ConcurrentHashMap<Object, List<RetroSubscriber>> registeredClasses = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, List<RetroSubscriber>> subscribersByTag = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, CacheableRequest> cachedResultsByTag = new ConcurrentHashMap<>();

    public <T> void addObservable(Observable<T> observable, final Class<T> clazz, final String tag,
                                  final boolean cacheResult, final boolean debounce) {

        Consumer<T> onNext = new Consumer<T>() {
            @Override
            public void accept(T response) throws Exception {
                if (cacheResult) {
                    cachedResultsByTag.put(tag, new CacheableRequest<>(response, null, false));
                    Log.d("RxRetroBus", "Adding " + tag + " to cachedResultsByTag");
                } else {
                    cachedResultsByTag.remove(tag);
                    Log.d("RxRetroBus", "Removing " + tag + " from cachedResultsByTag");
                }

                List<RetroSubscriber> subscribers = subscribersByTag.get(tag);
                if (subscribers != null) {
                    for (RetroSubscriber subscriber : subscribers) {
                        postSuccess(subscriber, response, tag);
                    }
                }
            }
        };

        Consumer<Throwable> onError = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (cacheResult) {
                    cachedResultsByTag.put(tag, new CacheableRequest<>(null, throwable, false));
                    Log.d("RxRetroBus", "Adding " + tag + " to cachedResultsByTag");
                } else {
                    cachedResultsByTag.remove(tag);
                    Log.d("RxRetroBus", "Removing " + tag + " from cachedResultsByTag");
                }

                List<RetroSubscriber> subscribers = subscribersByTag.get(tag);
                if (subscribers != null) {
                    for (RetroSubscriber subscriber : subscribers) {
                        postError(subscriber, throwable);
                    }
                }
            }
        };

        // If debounce is enabled and the call is request is still being made, do not make the call again
        if (debounce && (cachedResultsByTag.get(tag) != null && cachedResultsByTag.get(tag).isLoading())) {
            return;
        }

        cachedResultsByTag.put(tag, new CacheableRequest<>(null, null, true));
        Log.d("RxRetroBus", "Adding " + tag + " to cachedResultsByTag");

        List<RetroSubscriber> subscribers = subscribersByTag.get(tag);
        if (subscribers != null) {
            for (RetroSubscriber sub : subscribersByTag.get(tag)) {
                sub.onLoading();
            }
        }

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    public void register(Object object, List<RetroSubscriber> subscribers) {
        List<RetroSubscriber> unmodifiable = Collections.unmodifiableList(subscribers);
        registeredClasses.put(object,unmodifiable);
        Log.d("RxRetroBus", "Adding " + object.toString() + " to registeredClasses");
        addToSubscribersMap(unmodifiable);

        for (RetroSubscriber sub : unmodifiable) {
            String tag = sub.getTagName();
            CacheableRequest cachedResponse = cachedResultsByTag.get(sub.getTagName());
            if (cachedResponse != null) {
                if (cachedResponse.isError()) {
                    postError(sub, cachedResponse.getError());
                } else if (cachedResponse.isLoading()) {
                    postLoading(sub);
                } else {
                    postSuccess(sub, cachedResponse.getSuccess(), tag);
                }
            }
        }
    }

    public void unregister(Object object) {
        removeFromSubscribersMap(registeredClasses.get(object));
        registeredClasses.remove(object);
        Log.d("RxRetroBus", "Removing " + object.toString() + " from registeredClasses");
    }

    private void addToSubscribersMap(List<RetroSubscriber> subscribers) {
        for (RetroSubscriber subscriber : subscribers) {
            List<RetroSubscriber> originalList = subscribersByTag.get(subscriber.getTagName());
            List<RetroSubscriber> updatedList = new ArrayList<>();

            if (originalList != null) {
                updatedList.addAll(originalList);
            }

            updatedList.add(subscriber);

            subscribersByTag.put(subscriber.getTagName(), Collections.unmodifiableList(updatedList));
            Log.d("RxRetroBus", "Adding " + subscriber.getTagName() + " to subscribersByTag");
        }
    }

    private void removeFromSubscribersMap(List<RetroSubscriber> subscribers) {
        for (RetroSubscriber subscriber : subscribers) {
            List<RetroSubscriber> originalList = subscribersByTag.get(subscriber.getTagName());
            List<RetroSubscriber> updatedList = new ArrayList<>();

            updatedList.addAll(originalList);
            updatedList.remove(subscriber);

            subscribersByTag.put(subscriber.getTagName(), Collections.unmodifiableList(updatedList));
            Log.d("RxRetroBus", "Removing " + subscriber.getTagName() + " from subscribersByTag");
        }
    }

    private <T> void postSuccess(RetroSubscriber sub, T response, String tag) {
        try {
            sub.getClass().getMethod("onSuccess", response.getClass());
            sub.onSuccess(response);
        } catch (NoSuchMethodException nsme) {
            throw new IllegalArgumentException("Subscriber to the tag \"" + tag
                    + "\" provided an argument different from the tag's corresponding type"
                    + " of " + response.getClass().toString() + "."
            );
        }
    }

    private void postLoading(RetroSubscriber sub) {
        sub.onLoading();
    }

    private void postError(RetroSubscriber sub, Throwable error) {
        sub.onError(error);
    }
}
