package com.example.scorely.logic;

import com.example.scorely.model.MediaItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * מנוע ההמלצות. עיקרון: דירוג מעל הניטרלי (3) מחזק את תגיות הפריט, דירוג מתחתיו מחליש אותן.
 * פריט מומלץ לפי דמיון קוסינוס בין וקטור הטעם של המשתמש לוקטור התגיות של הפריט.
 * שימוש ב-PriorityQueue לשליפת Top-N ביעילות.
 */
public class RecommendationEngine {

    public static final double NEUTRAL_RATING = 3.0;

    /** פריט עם ציון התאמה. */
    public static class ScoredItem {
        public final MediaItem item;
        public final double score;
        public ScoredItem(MediaItem item, double score) { this.item = item; this.score = score; }
    }

    /** בונה וקטור טעם מדירוגי המשתמש. */
    public TasteVector buildTasteVector(Map<String, Float> ratingByItemId,
                                        Map<String, Set<String>> tagsByItemId) {
        TasteVector v = new TasteVector();
        for (Map.Entry<String, Float> e : ratingByItemId.entrySet()) {
            Set<String> tags = tagsByItemId.get(e.getKey());
            if (tags == null) continue;
            double w = e.getValue() - NEUTRAL_RATING;
            for (String t : tags) v.add(t, w);
        }
        return v;
    }

    /** וקטור של פריט: כל תגית במשקל 1. */
    public TasteVector vectorOf(Set<String> tags) {
        TasteVector v = new TasteVector();
        for (String t : tags) v.add(t, 1.0);
        return v;
    }

    /** מחזיר עד n המלצות ממוינות מהגבוה לנמוך, למעט פריטים שכבר דורגו. */
    public List<ScoredItem> recommend(TasteVector user, List<MediaItem> candidates,
                                      Set<String> excludeIds, int n) {
        PriorityQueue<ScoredItem> heap = new PriorityQueue<>((a, b) -> Double.compare(a.score, b.score));
        for (MediaItem m : candidates) {
            if (excludeIds.contains(m.getId())) continue;
            double s = user.cosine(vectorOf(m.getTags()));
            if (s <= 0) continue;
            heap.add(new ScoredItem(m, s));
            if (heap.size() > n) heap.poll();
        }
        List<ScoredItem> out = new ArrayList<>(heap);
        Collections.sort(out, (a, b) -> Double.compare(b.score, a.score));
        return out;
    }

    /** דמיון טעם בין שני משתמשים (להצעת עוקבים). */
    public double userSimilarity(TasteVector a, TasteVector b) {
        return a.cosine(b);
    }
}
