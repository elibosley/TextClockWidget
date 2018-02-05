package com.elijahbosley.textclockwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;

/**
 * Class to create a bitmap from a font - allows on the fly font swapping
 */
class BitmapCreator {

    /**
     * Creates and returns a new bitmap containing the given text. From stackoverflow/questions/318572
     */
    static Bitmap getFontBitmap(Context context, String[] lines, int textColor, float fontSizeSP, int typeFaceSelection, boolean backgroundEnabled, int backgroundColor) {
        Typeface typeface = Typeface.DEFAULT;
        switch (typeFaceSelection) {
            case 0:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoMono-Thin.ttf");
                break;
            case 1:
                typeface = Typeface.create("sans-serif-light", Typeface.NORMAL);
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
        //todo width and height 0 issue
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height * 3, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        // Draw the background if it's enabled
        if (backgroundEnabled) {
            Paint background = new Paint();
            background.setColor(backgroundColor); // change to background color
            canvas.drawRoundRect(0, 0, textWidth, height * 3, 15, 15, background);
        }
        canvas.drawText(lines[0], (float) pad + (paint.measureText(lines[0]) / 2), fontSizePX, paint);
        canvas.drawText(lines[1], (float) pad + (paint.measureText(lines[1]) / 2), fontSizePX * 2 + pad, paint);
        canvas.drawText(lines[2], (float) pad + (paint.measureText(lines[2]) / 2), fontSizePX * 3 + pad, paint);


        return bitmap;
    }

    private static int convertDiptoPix(Context context, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }
}
