package com.abhinav.aimlroadmap;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class RoadmapData {
    private static JSONObject db;

    private RoadmapData() {}

    private static JSONObject load(Context c) {
        if (db != null) return db;
        try (InputStream in = c.getAssets().open("roadmap-data.json")) {
            byte[] bytes = new byte[in.available()];
            int read = in.read(bytes);
            db = new JSONObject(new String(bytes, 0, read, StandardCharsets.UTF_8));
            return db;
        } catch (Exception e) {
            db = new JSONObject();
            return db;
        }
    }

    public static JSONObject day(Context c, int day) {
        try {
            JSONArray a = load(c).getJSONArray("days");
            return a.getJSONObject(Math.max(0, Math.min(a.length()-1, day-1)));
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static JSONObject dsaDay(Context c, int day) {
        try {
            JSONObject dsa = load(c).getJSONObject("dsa");
            JSONArray a = dsa.getJSONArray("daily");
            return a.getJSONObject(Math.max(0, Math.min(a.length()-1, day-1)));
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static JSONObject section(Context c, int section) {
        try {
            return load(c).getJSONObject("sections").getJSONObject(String.valueOf(section));
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static String safe(JSONObject o, String key) {
        return o.optString(key, "");
    }
}
