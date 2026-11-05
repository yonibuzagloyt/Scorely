package com.example.scorely.data.remote;

import com.example.scorely.model.Review;
import com.example.scorely.util.ResultCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * עטיפה ל-Cloud Firestore: כתיבה וקריאה של ביקורות, מעקב אחרי משתמשים,
 * ומאזין בזמן אמת לפיד החברים. אוספים: users, reviews.
 */
public class FirestoreService {

    /** ממשק מאזין לעדכוני פיד בזמן אמת. */
    public interface FeedListener {
        void onFeedChanged(List<Map<String, Object>> reviews);
        void onError(Exception e);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void pushReview(Review r, ResultCallback<Void> cb) {
        Map<String, Object> m = new HashMap<>();
        m.put("itemId", r.getItemId());
        m.put("userId", r.getUserId());
        m.put("rating", r.getRating());
        m.put("text", r.getText());
        m.put("createdAt", r.getCreatedAt());
        m.put("updatedAt", r.getUpdatedAt());
        db.collection("reviews").document(r.getId()).set(m)
                .addOnSuccessListener(v -> cb.onSuccess(null))
                .addOnFailureListener(cb::onError);
    }

    public void follow(String myUid, String otherUid, ResultCallback<Void> cb) {
        db.collection("users").document(myUid)
                .update("following", FieldValue.arrayUnion(otherUid))
                .addOnSuccessListener(v -> cb.onSuccess(null))
                .addOnFailureListener(cb::onError);
    }

    /** מאזין בזמן אמת לביקורות של המשתמשים שאחריהם עוקבים (whereIn מוגבל ל-10 ערכים). */
    public ListenerRegistration listenToFeed(List<String> followingIds, FeedListener listener) {
        if (followingIds.isEmpty()) {
            listener.onFeedChanged(new ArrayList<>());
            return () -> { };
        }
        Query q = db.collection("reviews")
                .whereIn("userId", followingIds.subList(0, Math.min(10, followingIds.size())))
                .orderBy("createdAt", Query.Direction.DESCENDING).limit(50);
        return q.addSnapshotListener((snap, e) -> {
            if (e != null) { listener.onError(e); return; }
            List<Map<String, Object>> out = new ArrayList<>();
            if (snap != null) for (DocumentSnapshot d : snap.getDocuments()) out.add(d.getData());
            listener.onFeedChanged(out);
        });
    }
}
