package com.abhinav.aimlroadmap;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONObject;

public class RoadmapWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_PREV = "com.abhinav.aimlroadmap.PREV";
    public static final String ACTION_NEXT = "com.abhinav.aimlroadmap.NEXT";
    public static final String ACTION_DONE = "com.abhinav.aimlroadmap.DONE";
    public static final String ACTION_MOVE = "com.abhinav.aimlroadmap.MOVE";

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        updateAll(context, manager);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        int day = RoadmapStore.getCurrentDay(context);

        if (ACTION_PREV.equals(action)) {
            RoadmapStore.setCurrentDay(context, day - 1);
        } else if (ACTION_NEXT.equals(action)) {
            RoadmapStore.setCurrentDay(context, day + 1);
        } else if (ACTION_DONE.equals(action)) {
            RoadmapStore.markDone(context, day);
            RoadmapStore.setCurrentDay(context, day + 1);
        } else if (ACTION_MOVE.equals(action)) {
            RoadmapStore.markSkipped(context, day);
            RoadmapStore.setCurrentDay(context, day + 1);
        }

        updateAll(context, AppWidgetManager.getInstance(context));
    }

    private static PendingIntent action(Context c, String action, int request) {
        Intent i = new Intent(c, RoadmapWidgetProvider.class);
        i.setAction(action);
        return PendingIntent.getBroadcast(
                c, request, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, android.os.Bundle newOptions) {
        updateAll(context, appWidgetManager);
    }

    public static void updateAll(Context c, AppWidgetManager manager) {
        int day = RoadmapStore.getCurrentDay(c);
        JSONObject d = RoadmapData.day(c, day);
        JSONObject ds = RoadmapData.dsaDay(c, day);

        String title = RoadmapData.safe(d, "title");
        if (title.isEmpty()) title = "Today's Target";

        String qtext = "";
        try {
            JSONArray qs = ds.getJSONArray("problems");
            for (int i = 0; i < Math.min(2, qs.length()); i++) {
                JSONObject q = qs.getJSONObject(i);
                if (i > 0) qtext += "  •  ";
                qtext += "NC-" + String.format("%03d", q.optInt("nc")) + " · LC-" + q.optInt("lc") +
                        " " + q.optString("title");
            }
        } catch (Exception ignored) {}

        ComponentName component = new ComponentName(c, RoadmapWidgetProvider.class);
        int[] ids = manager.getAppWidgetIds(component);
        if (ids == null || ids.length == 0) return;

        for (int appWidgetId : ids) {
            android.os.Bundle options = manager.getAppWidgetOptions(appWidgetId);
            int minHeight = options != null ? options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0) : 0;
            int minWidth = options != null ? options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0) : 0;
            boolean compact = minHeight > 0 && (minHeight < 175 || (minWidth > 0 && minWidth < 190));

            int layoutId = compact ? R.layout.widget_roadmap_compact : R.layout.widget_roadmap;
            RemoteViews v = new RemoteViews(c.getPackageName(), layoutId);
            v.setTextViewText(R.id.widgetDay, "DAY " + day + " / 130");
            v.setTextViewText(R.id.widgetTitle, title);
            v.setTextViewText(R.id.widgetDsa, qtext);
            Bitmap ring = CircleBitmap.make(RoadmapStore.completionPercent(c), compact ? 72 : 88);
            v.setImageViewBitmap(R.id.widgetProgress, ring);

            v.setOnClickPendingIntent(R.id.widgetPrev, action(c, ACTION_PREV, 101 + appWidgetId * 10));
            v.setOnClickPendingIntent(R.id.widgetDone, action(c, ACTION_DONE, 102 + appWidgetId * 10));
            v.setOnClickPendingIntent(R.id.widgetNext, action(c, ACTION_NEXT, 103 + appWidgetId * 10));

            Intent open = new Intent(c, MainActivity.class);
            PendingIntent openPi = PendingIntent.getActivity(
                    c, 104 + appWidgetId * 10, open,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            v.setOnClickPendingIntent(R.id.widgetTitle, openPi);
            v.setOnClickPendingIntent(R.id.widgetBrand, openPi);

            manager.updateAppWidget(appWidgetId, v);
        }
    }

}
