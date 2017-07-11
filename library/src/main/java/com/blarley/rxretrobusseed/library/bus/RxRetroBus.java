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

public class RxRetroBus {

    ConcurrentHashMap<RetroSubscriberReceiver, List<RetroSubscriber>> registeredClasses = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, List<RetroSubscriber>> subscribersByTag = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Request> resultsByTag = new ConcurrentHashMap<>();

    public <T> void addObservable(Observable<T> observable, final Class<T> clazz, final Publish request) {

        Consumer<T> onNext = new Consumer<T>() {
            @Override
            public void accept(T response) throws Exception {
                if (request.isCached() || request.isSticky()) {
                    resultsByTag.put(request.getTag(), new Request<>(response, null, false, request.isSticky()));
                    Log.d("RxRetroBus", "Adding " + request.getTag() + " to resultsByTag");
                } else {
                    resultsByTag.remove(request.getTag());
                    Log.d("RxRetroBus", "Removing " + request.getTag() + " from resultsByTag");
                }

                List<RetroSubscriber> subscribers = subscribersByTag.get(request.getTag());
                if (subscribers != null) {
                    for (RetroSubscriber subscriber : subscribers) {
                        postSuccess(subscriber, response, request.getTag());
                    }
                }
            }
        };

        Consumer<Throwable> onError = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (request.isCached() || request.isSticky()) {
                    resultsByTag.put(request.getTag(), new Request<>(null, throwable, false, request.isSticky()));
                    Log.d("RxRetroBus", "Adding " + request.getTag() + " to resultsByTag");
                } else {
                    resultsByTag.remove(request.getTag());
                    Log.d("RxRetroBus", "Removing " + request.getTag() + " from resultsByTag");
                }

                List<RetroSubscriber> subscribers = subscribersByTag.get(request.getTag());
                if (subscribers != null) {
                    for (RetroSubscriber subscriber : subscribers) {
                        postError(subscriber, throwable);
                    }
                }
            }
        };

        // If debounce is true and the request has not yet returned,
        // do not make the call again, regardless of whether it is cacheable
        if (request.isDebounced() && (resultsByTag.get(request.getTag()) != null && resultsByTag.get(request.getTag()).isLoading())) {
            return;
        }

        resultsByTag.put(request.getTag(), new Request<>(null, null, true, request.isSticky()));
        Log.d("RxRetroBus", "Adding " + request.getTag() + " to resultsByTag");

        List<RetroSubscriber> subscribers = subscribersByTag.get(request.getTag());
        if (subscribers != null) {
            for (RetroSubscriber subscriber : subscribersByTag.get(request.getTag())) {
                subscriber.onLoading();
            }
        }

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    public void register(RetroSubscriberReceiver receiver) {
        List<RetroSubscriber> unmodifiable = Collections.unmodifiableList(receiver.getSubscribers());
        registeredClasses.put(receiver, unmodifiable);
        Log.d("RxRetroBus", "Adding " + receiver.toString() + " to registeredClasses");
        addToSubscribersMap(unmodifiable);

        for (RetroSubscriber subscriber : unmodifiable) {
            String tag = subscriber.getTag();
            Request cachedResponse = resultsByTag.get(subscriber.getTag());
            if (cachedResponse != null) {
                if (cachedResponse.isLoading()) {
                    postLoading(subscriber);
                } else {
                    if (cachedResponse.isError()) {
                        postError(subscriber, cachedResponse.getError());
                    } else {
                        postSuccess(subscriber, cachedResponse.getSuccess(), tag);
                    }

                    if (cachedResponse.isSticky()) {
                        resultsByTag.remove(tag);
                    }
                }
            }
        }
    }

    public void unregister(RetroSubscriberReceiver receiver) {
        removeFromSubscribersMap(registeredClasses.get(receiver));
        registeredClasses.remove(receiver);
        Log.d("RxRetroBus", "Removing " + receiver.toString() + " from registeredClasses");
    }

    private void addToSubscribersMap(List<RetroSubscriber> subscribers) {
        for (RetroSubscriber subscriber : subscribers) {
            List<RetroSubscriber> originalList = subscribersByTag.get(subscriber.getTag());
            List<RetroSubscriber> updatedList = new ArrayList<>();

            if (originalList != null) {
                updatedList.addAll(originalList);
            }

            updatedList.add(subscriber);

            subscribersByTag.put(subscriber.getTag(), Collections.unmodifiableList(updatedList));
            Log.d("RxRetroBus", "Adding " + subscriber.getTag() + " to subscribersByTag");
        }
    }

    private void removeFromSubscribersMap(List<RetroSubscriber> subscribers) {
        for (RetroSubscriber subscriber : subscribers) {
            List<RetroSubscriber> originalList = subscribersByTag.get(subscriber.getTag());
            List<RetroSubscriber> updatedList = new ArrayList<>();

            updatedList.addAll(originalList);
            updatedList.remove(subscriber);

            subscribersByTag.put(subscriber.getTag(), Collections.unmodifiableList(updatedList));
            Log.d("RxRetroBus", "Removing " + subscriber.getTag() + " from subscribersByTag");
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
