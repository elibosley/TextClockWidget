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
    public static Bitmap getFontBitmap(Context context, String text, int color, float fontSizeSP, int typeFaceSelection) {
        Typeface typeface = Typeface.DEFAULT;
        switch (typeFaceSelection) {
            case 0:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoMono-Regular.ttf");
                break;
            case 1:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoMono-Thin.ttf");
                break;
        }
        int fontSizePX = convertDiptoPix(context, fontSizeSP);
        int pad = (fontSizePX / 9);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setColor(color);
        paint.setTextSize(fontSizePX);

        int textWidth = (int) (paint.measureText(text) + pad * 2);
        int height = (int) (fontSizePX / 0.75);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        float xOriginal = pad;
        canvas.drawText(text, xOriginal, fontSizePX, paint);
        return bitmap;
    }

    public static int convertDiptoPix(Context context, float dip) {
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return value;
    }
}
