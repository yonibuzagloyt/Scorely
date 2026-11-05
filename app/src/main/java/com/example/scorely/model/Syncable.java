package com.example.scorely.model;

/** ממשק לעצם שמסונכרן בין Room ל-Firestore. חותמת הזמן משמשת לפתרון קונפליקטים (last-write-wins). */
public interface Syncable {
    String getId();
    long getUpdatedAt();
}
