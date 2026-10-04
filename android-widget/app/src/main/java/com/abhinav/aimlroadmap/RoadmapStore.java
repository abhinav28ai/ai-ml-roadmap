package com.abhinav.aimlroadmap;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public final class RoadmapStore {
    private static final String PREF = "roadmap_state";
    private static final String CURRENT = "current_day";
    private static final String DONE = "done_days";
    private static final String SKIPPED = "skipped_days";
    private static final String DEFERRED = "deferred_days";
    private static final String DSA = "dsa_solved";

    private RoadmapStore() {}

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static int getCurrentDay(Context c) {
        return Math.max(1, Math.min(130, prefs(c).getInt(CURRENT, 1)));
    }

    public static void setCurrentDay(Context c, int day) {
        prefs(c).edit().putInt(CURRENT, Math.max(1, Math.min(130, day))).apply();
    }

    private static Set<String> getSet(Context c, String key) {
        return new HashSet<>(prefs(c).getStringSet(key, new HashSet<>()));
    }

    private static void putSet(Context c, String key, Set<String> set) {
        prefs(c).edit().putStringSet(key, new HashSet<>(set)).apply();
    }

    public static boolean isDone(Context c, int day) {
        return getSet(c, DONE).contains(String.valueOf(day));
    }

    public static void markDone(Context c, int day) {
        Set<String> s = getSet(c, DONE);
        s.add(String.valueOf(day));
        putSet(c, DONE, s);
        removeFrom(c, SKIPPED, day);
        removeFrom(c, DEFERRED, day);
    }

    public static void markSkipped(Context c, int day) {
        Set<String> s = getSet(c, SKIPPED);
        s.add(String.valueOf(day));
        putSet(c, SKIPPED, s);
        removeFrom(c, DONE, day);
    }

    public static void markDeferred(Context c, int day) {
        Set<String> s = getSet(c, DEFERRED);
        s.add(String.valueOf(day));
        putSet(c, DEFERRED, s);
        removeFrom(c, DONE, day);
    }

    private static void removeFrom(Context c, String key, int day) {
        Set<String> s = getSet(c, key);
        if (s.remove(String.valueOf(day))) putSet(c, key, s);
    }

    public static boolean isDsaSolved(Context c, int lc) {
        return getSet(c, DSA).contains(String.valueOf(lc));
    }

    public static void toggleDsa(Context c, int lc) {
        Set<String> s = getSet(c, DSA);
        String k = String.valueOf(lc);
        if (!s.remove(k)) s.add(k);
        putSet(c, DSA, s);
    }

    public static int doneCount(Context c) {
        return getSet(c, DONE).size();
    }

    public static int dsaCount(Context c) {
        return getSet(c, DSA).size();
    }

    public static int completionPercent(Context c) {
        return Math.round(doneCount(c) * 100f / 130f);
    }
}
