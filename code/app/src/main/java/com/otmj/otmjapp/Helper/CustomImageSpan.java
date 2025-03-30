package com.otmj.otmjapp.Helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.style.ImageSpan;

public class CustomImageSpan extends ImageSpan {
    public CustomImageSpan(Drawable drawable) {
        super(drawable, ALIGN_BASELINE); // Start with baseline alignment
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        int size = super.getSize(paint, text, start, end, fm);
        if (fm != null) {
            fm.ascent -= size / 6; // Move up slightly
            fm.descent += size / 6; // Move down slightly
        }
        return size;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();

        // Move the emoji lower by increasing the adjustment value
        int transY = bottom - drawable.getBounds().bottom + (drawable.getBounds().height() / 6); // Increase offset
        canvas.translate(x, transY);

        drawable.draw(canvas);
        canvas.restore();
    }

}
