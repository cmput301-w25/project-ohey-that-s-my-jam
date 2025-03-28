package com.otmj.otmjapp.Helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ImageSpan;


/**
 * Class that adjusts the format of an image so that is aligned nicely next to some text.
 */
public class CustomImageSpan extends ImageSpan {

    /**
     * Creates a CustomImageSpan using the given image.
     * It starts with a baseline alignment to match text placement.
     *
     * @param drawable The image to be used in the text.
     */
    public CustomImageSpan(Drawable drawable) {
        super(drawable, ALIGN_BASELINE); // Start with baseline alignment
    }

    /**
     * Calculates how much space that the image takes up in the text and slightly
     * adjusts its vertical position for better alignment.
     *
     * @param paint The paint used to draw the text.
     * @param text The full text that contains this image.
     * @param start The start position of the image in the text.
     * @param end The end position of the image in the text.
     * @param fm The font metrics (used to adjust spacing), can be null.
     * @return The width of the image inside the text.
     */
    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        int size = super.getSize(paint, text, start, end, fm);
        if (fm != null) {
            fm.ascent -= size / 6; // Move up slightly
            fm.descent += size / 6; // Move down slightly
        }
        return size;
    }

    /**
     * Draws the image inside the text at the right position.
     * Makes sure that the image doesn't sit too high or too low compared to the surrounding text.
     *
     * @param canvas The canvas where the image will be drawn.
     * @param text The text that contains this image.
     * @param start The start position of the image in the text.
     * @param end The end position of the image in the text.
     * @param x The x-coordinate where the image starts.
     * @param top The top boundary of the text line.
     * @param y The baseline position of the text.
     * @param bottom The bottom boundary of the text line.
     * @param paint The paint used for drawing the text.
     */
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
