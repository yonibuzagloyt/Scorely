package com.example.scorely.util;

/** ממשק קולבק כללי לפעולות אסינכרוניות (רשת, Firestore). */
public interface ResultCallback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}
