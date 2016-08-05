package com.elijahbosley.textclockwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;

/**
 * Created by elibosley on 7/24/16.
 */
public class BitmapCreator {

    /**
     * Creates and returns a new bitmap containing the given text. From stackoverflow/questions/318572
     */
    public static Bitmap getFontBitmap(Context context, String[] lines, int textColor, float fontSizeSP, int typeFaceSelection) {
        Typeface typeface = Typeface.DEFAULT;
        switch (typeFaceSelection) {
            case 0:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoMono-Regular.ttf");
                break;
            case 1:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoMono-Thin.ttf");
                break;
        }
        String longest = "";
        for (String name : lines) {
            longest = name.length() > longest.length() ? name : longest;
        }

        int fontSizePX = convertDiptoPix(context, fontSizeSP);
        int pad = (fontSizePX / 9);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setColor(textColor);
        paint.setTextSize(fontSizePX);
        paint.setTextAlign(Paint.Align.CENTER);
        int textWidth = (int) (paint.measureText(longest) + pad * 2);
        int height = (int) (fontSizePX / 0.75);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height * 3, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        float xOriginal = pad;
        canvas.drawText(lines[0], xOriginal + paint.measureText(lines[0]) / 2, fontSizePX, paint);
        canvas.drawText(lines[1], xOriginal + paint.measureText(lines[1]) / 2, fontSizePX * 2 + pad, paint);
        canvas.drawText(lines[2], xOriginal + paint.measureText(lines[2]) / 2, fontSizePX * 3 + pad, paint);
        return bitmap;
    }

    public static int convertDiptoPix(Context context, float dip) {
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return value;
    }
}
