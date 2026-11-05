package com.example.scorely.logic;

import java.util.HashMap;
import java.util.Map;

/** וקטור טעם: מיפוי תגית→משקל. משמש לחישוב דמיון קוסינוס בין משתמשים ופריטים. */
public class TasteVector {

    private final Map<String, Double> weights = new HashMap<>();

    public void add(String tag, double delta) {
        Double cur = weights.get(tag);
        weights.put(tag, (cur == null ? 0 : cur) + delta);
    }

    public double get(String tag) {
        Double v = weights.get(tag);
        return v == null ? 0 : v;
    }

    public double norm() {
        double sum = 0;
        for (double v : weights.values()) sum += v * v;
        return Math.sqrt(sum);
    }

    /** דמיון קוסינוס בין -1 ל-1. מחזיר 0 אם אחד הוקטורים ריק. */
    public double cosine(TasteVector other) {
        double n1 = norm(), n2 = other.norm();
        if (n1 == 0 || n2 == 0) return 0;
        double dot = 0;
        for (Map.Entry<String, Double> e : weights.entrySet()) dot += e.getValue() * other.get(e.getKey());
        return dot / (n1 * n2);
    }

    public Map<String, Double> asMap() { return weights; }
}
