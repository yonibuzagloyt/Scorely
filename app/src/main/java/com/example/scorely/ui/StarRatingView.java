package com.example.scorely.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * View מותאם אישית (ירושה מ-View): דירוג בחצאי כוכבים המצויר על Canvas.
 * תומך בלחיצה וגרירה, ומדווח למאזין על שינוי הדירוג.
 */
public class StarRatingView extends View {

    /** מאזין לשינוי דירוג. */
    public interface OnRatingChangeListener {
        void onRatingChanged(float rating, boolean fromUser);
    }

    private static final int STARS = 5;

    private final Paint emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float rating = 0f;
    private boolean readOnly = false;
    private OnRatingChangeListener listener;

    public StarRatingView(Context c) { super(c); init(); }
    public StarRatingView(Context c, AttributeSet a) { super(c, a); init(); }
    public StarRatingView(Context c, AttributeSet a, int s) { super(c, a, s); init(); }

    private void init() {
        emptyPaint.setStyle(Paint.Style.STROKE);
        emptyPaint.setStrokeWidth(4f);
        emptyPaint.setColor(Color.GRAY);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#FFB300"));
    }

    public void setRating(float r) { setRatingInternal(r, false); }
    public float getRating() { return rating; }
    public void setReadOnly(boolean ro) { readOnly = ro; }
    public void setOnRatingChangeListener(OnRatingChangeListener l) { listener = l; }

    private void setRatingInternal(float r, boolean fromUser) {
        float clamped = Math.max(0f, Math.min(STARS, r));
        if (clamped == rating) return;
        rating = clamped;
        invalidate();
        if (listener != null) listener.onRatingChanged(rating, fromUser);
    }

    private Path starPath(float cx, float cy, float outer) {
        float inner = outer * 0.45f;
        Path p = new Path();
        for (int i = 0; i < 10; i++) {
            double ang = -Math.PI / 2 + i * Math.PI / 5;
            float r = (i % 2 == 0) ? outer : inner;
            float x = cx + (float) (Math.cos(ang) * r);
            float y = cy + (float) (Math.sin(ang) * r);
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.close();
        return p;
    }

    @Override
    protected void onMeasure(int wSpec, int hSpec) {
        int defW = (int) (getResources().getDisplayMetrics().density * 200);
        int w = resolveSize(defW, wSpec);
        int h = resolveSize(w / STARS, hSpec);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float cell = getWidth() / (float) STARS;
        float outer = Math.min(cell, getHeight()) / 2f * 0.9f;
        float cy = getHeight() / 2f;
        for (int i = 0; i < STARS; i++) {
            float cx = (i + 0.5f) * cell;
            Path star = starPath(cx, cy, outer);
            float fraction = Math.max(0f, Math.min(1f, rating - i)); // 0, 0.5 או 1
            if (fraction > 0f) {
                canvas.save();
                float left = cx - outer;
                canvas.clipRect(left, 0, left + 2 * outer * fraction, getHeight());
                canvas.drawPath(star, fillPaint);
                canvas.restore();
            }
            canvas.drawPath(star, emptyPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (readOnly) return false;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                float raw = e.getX() / getWidth() * STARS;
                float half = (float) Math.ceil(raw * 2) / 2f; // עיגול לחצי כוכב כלפי מעלה
                setRatingInternal(Math.max(0.5f, half), true);
                return true;
            case MotionEvent.ACTION_UP:
                performClick();
                return true;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean performClick() { return super.performClick(); }
}
