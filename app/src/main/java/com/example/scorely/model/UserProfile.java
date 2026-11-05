package com.example.scorely.model;

import java.util.HashSet;
import java.util.Set;

/** פרופיל משתמש. מכיל קבוצת עוקבים (גרף חברתי) ללא כפילויות. */
public class UserProfile {

    private final String uid;
    private String displayName;
    private String photoUri;
    private final Set<String> following = new HashSet<>();

    public UserProfile(String uid, String displayName) {
        this.uid = uid;
        this.displayName = displayName;
    }

    public boolean follow(String otherUid) { return !uid.equals(otherUid) && following.add(otherUid); }
    public boolean unfollow(String otherUid) { return following.remove(otherUid); }
    public boolean isFollowing(String otherUid) { return following.contains(otherUid); }

    public String getUid() { return uid; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String n) { displayName = n; }
    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String uri) { photoUri = uri; }
    public Set<String> getFollowing() { return following; }
}
