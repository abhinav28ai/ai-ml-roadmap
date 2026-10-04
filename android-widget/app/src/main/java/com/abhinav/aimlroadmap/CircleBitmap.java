package com.abhinav.aimlroadmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

public final class CircleBitmap {
    private CircleBitmap() {}

    public static Bitmap make(int percent, int size) {
        Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        float cx = size / 2f, cy = size / 2f, r = size * 0.40f;

        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setStyle(Paint.Style.STROKE);
        bg.setStrokeWidth(size * 0.10f);
        bg.setColor(0xFF333333);
        c.drawCircle(cx, cy, r, bg);

        Paint fg = new Paint(Paint.ANTI_ALIAS_FLAG);
        fg.setStyle(Paint.Style.STROKE);
        fg.setStrokeWidth(size * 0.10f);
        fg.setStrokeCap(Paint.Cap.ROUND);
        fg.setColor(0xFFE0E0E0);
        c.drawArc(cx-r, cy-r, cx+r, cy+r, -90f, percent * 3.6f, false, fg);

        Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
        text.setColor(0xFFE0E0E0);
        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        text.setTextAlign(Paint.Align.CENTER);
        text.setTextSize(size * 0.26f);
        c.drawText(percent + "%", cx, cy + size * 0.09f, text);

        return b;
    }
}
