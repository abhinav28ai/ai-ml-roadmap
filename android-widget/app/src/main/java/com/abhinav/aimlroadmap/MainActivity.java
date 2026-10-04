package com.abhinav.aimlroadmap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private LinearLayout root;
    private TextView dayLabel, title, summary, mlTarget, project, revision, dsaTitle, dsa1, dsa2, stats;
    private ImageView circle;
    private int day;

    private static final int BG = Color.rgb(18,18,18);
    private static final int SURFACE = Color.rgb(30,30,30);
    private static final int SURFACE2 = Color.rgb(39,39,39);
    private static final int TEXT = Color.rgb(224,224,224);
    private static final int MUTED = Color.rgb(176,176,176);

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        day = RoadmapStore.getCurrentDay(this);
        buildUi();
        render();
    }

    @Override
    protected void onResume() {
        super.onResume();
        day = RoadmapStore.getCurrentDay(this);
        if (root != null) render();
    }

    private TextView tv(String text, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(0, 5, 0, 5);
        return t;
    }

    private LinearLayout card() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(15, 15, 15, 15);
        l.setBackgroundResource(R.drawable.card_bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 8, 0, 0);
        l.setLayoutParams(p);
        return l;
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(11);
        b.setTextColor(TEXT);
        b.setAllCaps(false);
        b.setBackgroundResource(R.drawable.button_bg);
        b.setMinHeight(0);
        b.setMinWidth(0);
        b.setPadding(8, 5, 8, 5);
        return b;
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(14, 14, 14, 22);
        scroll.addView(root);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.addView(tv("AI ML ROADMAP", 13, MUTED, true));

        LinearLayout nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER_VERTICAL);
        Button prev = button("‹");
        Button next = button("›");
        dayLabel = tv("DAY 1 / 130", 11, MUTED, true);
        dayLabel.setGravity(Gravity.CENTER);
        nav.addView(prev, new LinearLayout.LayoutParams(48, 44));
        nav.addView(dayLabel, new LinearLayout.LayoutParams(0, 44, 1));
        nav.addView(next, new LinearLayout.LayoutParams(48, 44));
        header.addView(nav);

        prev.setOnClickListener(v -> { if (day > 1) { day--; RoadmapStore.setCurrentDay(this, day); render(); }});
        next.setOnClickListener(v -> { if (day < 130) { day++; RoadmapStore.setCurrentDay(this, day); render(); }});

        root.addView(header);

        LinearLayout target = card();
        target.addView(tv("TODAY'S TARGET", 10, MUTED, true));
        title = tv("", 22, TEXT, true); title.setPadding(0, 8, 0, 3); target.addView(title);
        summary = tv("", 14, MUTED, false); target.addView(summary);
        root.addView(target);

        LinearLayout ml = card();
        ml.addView(tv("AI / ML", 18, TEXT, true));
        mlTarget = tv("", 14, MUTED, false); ml.addView(mlTarget);
        project = tv("", 13, TEXT, false); ml.addView(project);
        revision = tv("", 13, TEXT, false); ml.addView(revision);

        LinearLayout mlButtons = new LinearLayout(this);
        Button details = button("Section details · certification · research");
        mlButtons.addView(details, new LinearLayout.LayoutParams(-1, 46));
        ml.addView(mlButtons);
        details.setOnClickListener(v -> openSectionDetails());

        root.addView(ml);

        LinearLayout dsa = card();
        dsa.addView(tv("DSA · 2 QUESTIONS", 18, TEXT, true));
        dsaTitle = tv("", 13, MUTED, true); dsa.addView(dsaTitle);
        dsa1 = tv("", 13, TEXT, false); dsa.addView(dsa1);
        dsa2 = tv("", 13, TEXT, false); dsa.addView(dsa2);
        root.addView(dsa);

        LinearLayout progress = card();
        LinearLayout pgTop = new LinearLayout(this);
        pgTop.setGravity(Gravity.CENTER_VERTICAL);
        circle = new ImageView(this);
        pgTop.addView(circle, new LinearLayout.LayoutParams(120, 120));
        stats = tv("", 14, MUTED, false);
        stats.setPadding(14,0,0,0);
        pgTop.addView(stats, new LinearLayout.LayoutParams(0, -2, 1));
        progress.addView(pgTop);

        LinearLayout actions = new LinearLayout(this);
        Button done = button("✓ DONE → NEXT");
        Button move = button("→ MOVE AHEAD");
        Button later = button("↻ RESCHEDULE");
        actions.addView(done, new LinearLayout.LayoutParams(0, 50, 1));
        actions.addView(move, new LinearLayout.LayoutParams(0, 50, 1));
        actions.addView(later, new LinearLayout.LayoutParams(0, 50, 1));
        progress.addView(actions);

        done.setOnClickListener(v -> { RoadmapStore.markDone(this, day); if(day<130)day++; RoadmapStore.setCurrentDay(this,day); refreshWidget(); render(); });
        move.setOnClickListener(v -> { RoadmapStore.markSkipped(this, day); if(day<130)day++; RoadmapStore.setCurrentDay(this,day); refreshWidget(); render(); });
        later.setOnClickListener(v -> { RoadmapStore.markDeferred(this, day); if(day<130)day++; RoadmapStore.setCurrentDay(this,day); refreshWidget(); render(); });

        root.addView(progress);

        Button openWeb = button("Open complete web roadmap");
        LinearLayout.LayoutParams webP = new LinearLayout.LayoutParams(-1, 48);
        webP.setMargins(0, 10, 0, 0);
        root.addView(openWeb, webP);
        openWeb.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://abhinav28ai.github.io/ai-ml-roadmap/?v=5"));
            startActivity(i);
        });

        setContentView(scroll);
    }

    private void render() {
        JSONObject d = RoadmapData.day(this, day);
        JSONObject ds = RoadmapData.dsaDay(this, day);

        dayLabel.setText("DAY " + day + " / 130\n" + RoadmapData.safe(d, "phase"));
        title.setText(RoadmapData.safe(d, "title"));
        summary.setText(RoadmapData.safe(d, "type").equals("project")
                ? "Project / revision day — finish the build and your 2 DSA questions."
                : "Study target — finish the lesson actively and your 2 DSA questions.");
        mlTarget.setText(RoadmapData.safe(d, "target"));
        project.setText("\nProject: " + RoadmapData.safe(d, "project"));
        revision.setText("\nRevision: " + RoadmapData.safe(d, "revision"));

        try {
            JSONArray qs = ds.getJSONArray("problems");
            dsaTitle.setText(ds.optString("mode", "NEW") + " · 2 QUESTIONS");
            dsa1.setText("Q1 · NC-" + String.format("%03d", qs.getJSONObject(0).optInt("nc")) +
                    " · LC-" + qs.getJSONObject(0).optInt("lc") + "\n" +
                    qs.getJSONObject(0).optString("title") + " · " +
                    qs.getJSONObject(0).optString("difficulty") + " · " +
                    qs.getJSONObject(0).optString("pattern"));
            dsa2.setText("Q2 · NC-" + String.format("%03d", qs.getJSONObject(1).optInt("nc")) +
                    " · LC-" + qs.getJSONObject(1).optInt("lc") + "\n" +
                    qs.getJSONObject(1).optString("title") + " · " +
                    qs.getJSONObject(1).optString("difficulty") + " · " +
                    qs.getJSONObject(1).optString("pattern"));
        } catch (Exception ignored) {}

        int pct = RoadmapStore.completionPercent(this);
        circle.setImageBitmap(CircleBitmap.make(pct, 240));
        stats.setText("AI / ML\n" + RoadmapStore.doneCount(this) + " / 130 days\n\nDSA\n" +
                RoadmapStore.dsaCount(this) + " / 150 core solved");
    }

    private void openSectionDetails() {
        int section = 18;
        try {
            section = Integer.parseInt(currentDaySection());
        } catch (Exception ignored) {}

        JSONObject s = RoadmapData.section(this, section);
        String text = "WHERE TO STUDY\n" + RoadmapData.safe(s,"study") +
                "\n\nCREDENTIAL / PROOF\n" + RoadmapData.safe(s,"credential") +
                "\n\nRESEARCH / READING\n" + RoadmapData.safe(s,"read") +
                "\n\nPROJECT\n" + RoadmapData.safe(s,"project") +
                "\n\nREVISION\n" + RoadmapData.safe(s,"revision");
        new android.app.AlertDialog.Builder(this)
                .setTitle("Section " + section + " · " + RoadmapData.safe(s,"title"))
                .setMessage(text)
                .setPositiveButton("Close", null)
                .show();
    }

    private String currentDaySection() {
        try { return RoadmapData.day(this, day).optString("section","18").replace("S",""); }
        catch (Exception e) { return "18"; }
    }

    private void refreshWidget() {
        RoadmapWidgetProvider.updateAll(this,
                android.appwidget.AppWidgetManager.getInstance(this));
    }
}
