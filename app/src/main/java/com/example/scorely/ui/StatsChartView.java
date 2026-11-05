package com.example.scorely.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/** View מותאם אישית: גרף עמודות פשוט (למשל התפלגות הדירוגים של המשתמש). */
public class StatsChartView extends View {

    /** עמודה בגרף. */
    public static class Bar {
        public final String label;
        public final float value;
        public Bar(String label, float value) { this.label = label; this.value = value; }
    }

    private final List<Bar> bars = new ArrayList<>();
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint();

    public StatsChartView(Context c) { super(c); init(); }
    public StatsChartView(Context c, AttributeSet a) { super(c, a); init(); }
    public StatsChartView(Context c, AttributeSet a, int s) { super(c, a, s); init(); }

    private void init() {
        float sp = getResources().getDisplayMetrics().scaledDensity;
        barPaint.setColor(Color.parseColor("#3F51B5"));
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(12 * sp);
        textPaint.setTextAlign(Paint.Align.CENTER);
        axisPaint.setColor(Color.GRAY);
        axisPaint.setStrokeWidth(2f);
    }

    public void setData(List<Bar> data) {
        bars.clear();
        bars.addAll(data);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bars.isEmpty()) return;
        float pad = textPaint.getTextSize() * 1.6f;
        float chartH = getHeight() - 2 * pad;
        float baseline = getHeight() - pad;
        float max = 0;
        for (Bar b : bars) max = Math.max(max, b.value);
        if (max == 0) return;

        float slot = getWidth() / (float) bars.size();
        float barW = slot * 0.6f;
        canvas.drawLine(0, baseline, getWidth(), baseline, axisPaint);
        for (int i = 0; i < bars.size(); i++) {
            Bar b = bars.get(i);
            float cx = slot * (i + 0.5f);
            float h = chartH * (b.value / max);
            canvas.drawRect(cx - barW / 2, baseline - h, cx + barW / 2, baseline, barPaint);
            canvas.drawText(String.valueOf((int) b.value), cx, baseline - h - 6, textPaint);
            canvas.drawText(b.label, cx, baseline + pad * 0.8f, textPaint);
        }
    }
}
