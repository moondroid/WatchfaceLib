package it.moondroid.watchfacelib.ui;

/**
 * Created by marco.granatiero on 22/01/2015.
 */

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.TextView;
import it.moondroid.watchfacelib.ui.ScalingUtilities.ScalingLogic;


class Scale {

    Scale() {
    }

    public static Drawable scaleDrawable(int resourceId, Context context, float scale) {
        return scaleDrawable(resourceId, context, scale, false);
    }

    public static Drawable scaleDrawable(int resourceId, Context context, float scale, boolean recycle) {
        Drawable drawable;
        try {
            drawable = context.getResources().getDrawable(resourceId);
            if (drawable != null) {
                drawable = drawable.mutate();
            }
        } catch (NotFoundException e) {
            Log.w("Scale", e);
            drawable = null;
        }

        if (drawable == null) {
            return null;
        }
        if (scale > 0.95f && scale < 1.05f) {
            return drawable;
        }
        int w = Math.round(((float) drawable.getIntrinsicWidth()) * scale);
        int h = Math.round(((float) drawable.getIntrinsicHeight()) * scale);
        Bitmap bitmap = ScalingUtilities.decodeResource(context.getResources(), resourceId, w, h, ScalingLogic.FIT);
        Bitmap newBitmap = ScalingUtilities.createScaledBitmap(bitmap, w, h, ScalingLogic.FIT);
        if (recycle) {
            bitmap.recycle();
        }
        return new BitmapDrawable(context.getResources(), newBitmap);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, Context context, float scale, boolean recycle) {
        if (bitmap == null) {
            return null;
        }
        if (scale > 0.95f && scale < 1.05f) {
            return bitmap;
        }
        Bitmap newBitmap = ScalingUtilities.createScaledBitmap(bitmap, Math.round(((float) bitmap.getWidth()) * scale), Math.round(((float) bitmap.getHeight()) * scale), ScalingLogic.FIT);
        if (recycle) {
            bitmap.recycle();
        }
        return newBitmap;
    }

    public static void setTextSizePx(TextView tv, int pxSize) {
        pxSize = Math.round(((float) pxSize) * 1.0f);
        if (pxSize % 2 > 0) {
            pxSize++;
        }
        tv.setTextSize(0, (float) pxSize);
    }

    public static void setTextSizeResource(TextView tv, int dimenResourceId) {
        setTextSizePx(tv, tv.getContext().getResources().getDimensionPixelSize(dimenResourceId));
    }

    public static int getDimenPX(Context c, int dimenId, float scale) {
        return Math.round(((float) c.getResources().getDimensionPixelSize(dimenId)) * scale);
    }
}
