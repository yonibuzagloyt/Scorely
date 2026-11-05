package com.example.scorely.model;

/** סטטוס פריט ברשימה האישית (Backlog) של המשתמש. */
public enum ListStatus {
    WANT("רוצה לצרוך"),
    IN_PROGRESS("כרגע"),
    COMPLETED("סיימתי"),
    DROPPED("נטשתי"),
    FAVORITE("מועדפים");

    private final String label;

    ListStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
