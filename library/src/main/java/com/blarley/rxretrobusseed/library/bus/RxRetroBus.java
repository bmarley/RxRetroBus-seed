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

    ConcurrentHashMap<Object, List<RetroSubscriber>> registeredClasses =
            new ConcurrentHashMap<Object, List<RetroSubscriber>>();

    ConcurrentHashMap<String, List<RetroSubscriber>> subscribersByTag =
            new ConcurrentHashMap<String, List<RetroSubscriber>>();

    ConcurrentHashMap<String, CacheableResponse> cachedResultsByTag =
            new ConcurrentHashMap<String, CacheableResponse>();

    public <T> void addObservable(Observable<T> observable, final Class<T> clazz, final String tag, boolean cacheResult) {

        Consumer<T> onNext = new Consumer<T>() {
            @Override
            public void accept(T t) throws Exception {
                cachedResultsByTag.put(tag, new CacheableResponse<>(clazz, t));
                Log.d("test", "WE HERE");
                for (RetroSubscriber sub : subscribersByTag.get(tag)) {
                    sub.onSuccess(t);
                    sub.onLoading();
                    sub.onError(new Throwable("yolo"));
                }
            }
        };

        Consumer<Throwable> onError = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    public void register(Object object, List<RetroSubscriber> subscribers) {
        List<RetroSubscriber> unmodifiable = Collections.unmodifiableList(subscribers);
        registeredClasses.put(object,unmodifiable);
        addToSubscribersMap(unmodifiable);
    }

    public void unregister(Object object) {
        removeFromSubscribersMap(registeredClasses.get(object));
        registeredClasses.remove(object);
    }

    private void addToSubscribersMap(List<RetroSubscriber> subscribers) {
        for ( RetroSubscriber subscriber : subscribers) {
            List<RetroSubscriber> originalList = subscribersByTag.get(subscriber.getTagName());
            List<RetroSubscriber> updatedList = new ArrayList<RetroSubscriber>();

            if (originalList != null) {
                updatedList.addAll(originalList);
            }

            updatedList.add(subscriber);

            subscribersByTag.put(subscriber.getTagName(), Collections.unmodifiableList(updatedList));
        }
    }

    private void removeFromSubscribersMap(List<RetroSubscriber> subscribers) {
        for ( RetroSubscriber subscriber : subscribers) {
            List<RetroSubscriber> originalList = subscribersByTag.get(subscriber.getTagName());
            List<RetroSubscriber> updatedList = new ArrayList<RetroSubscriber>();

            updatedList.addAll(originalList);
            updatedList.remove(subscriber);

            subscribersByTag.put(subscriber.getTagName(), Collections.unmodifiableList(updatedList));
        }
    }
}
