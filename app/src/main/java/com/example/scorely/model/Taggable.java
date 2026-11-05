package com.example.scorely.model;

import java.util.Set;

/** ממשק לכל עצם שיש לו תגיות (ז'אנרים / אווירה). בסיס ל"פרופיל הטעם" המשותף למשחקים ולמוזיקה. */
public interface Taggable {
    Set<String> getTags();
}
