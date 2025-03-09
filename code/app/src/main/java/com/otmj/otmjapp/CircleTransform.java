package com.otmj.otmjapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import com.squareup.picasso.Transformation;

public class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight()); // Determine the size of the circle

        // Create the square bitmap from the center of the source image
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);

        if (squaredBitmap != source) {
            source.recycle(); // Recycle the original image if it's not reused
        }

        // Create a new Bitmap for the circular image
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true); // Smooth the edges of the circle

        float r = size / 2f; // The radius of the circle
        canvas.drawCircle(r, r, r, paint); // Draw the circular image

        squaredBitmap.recycle(); // Recycle the squared bitmap

        return bitmap; // Return the circular image
    }

    @Override
    public String key() {
        return "circle"; // This is the key used to cache the transformation
    }
}
