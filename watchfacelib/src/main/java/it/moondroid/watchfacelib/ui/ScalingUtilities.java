package it.moondroid.watchfacelib.ui;

/**
 * Created by marco.granatiero on 22/01/2015.
 */
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

class ScalingUtilities {

    public enum ScalingLogic {
        CROP,
        FIT;

//        static {
//            CROP = new co.smartwatchface.library.ui.ScalingUtilities.ScalingLogic("CROP", 0);
//            FIT = new co.smartwatchface.library.ui.ScalingUtilities.ScalingLogic("FIT", 1);
//            ENUM$VALUES = new co.smartwatchface.library.ui.ScalingUtilities.ScalingLogic[]{CROP, FIT};
//        }
    }

    ScalingUtilities() {
    }

    public static Bitmap decodeResource(Resources res, int resId, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth, dstHeight, scalingLogic);
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Config.ARGB_8888);
        new Canvas(scaledBitmap).drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(2));
        return scaledBitmap;
    }

    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        return scalingLogic == ScalingLogic.FIT ? ((float) srcWidth) / ((float) srcHeight) > ((float) dstWidth) / ((float) dstHeight) ? srcWidth / dstWidth : srcHeight / dstHeight : ((float) srcWidth) / ((float) srcHeight) > ((float) dstWidth) / ((float) dstHeight) ? srcHeight / dstHeight : srcWidth / dstWidth;
    }

    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic != ScalingLogic.CROP) {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
        float dstAspect = ((float) dstWidth) / ((float) dstHeight);
        if (((float) srcWidth) / ((float) srcHeight) > dstAspect) {
            int srcRectWidth = (int) (((float) srcHeight) * dstAspect);
            int srcRectLeft = (srcWidth - srcRectWidth) / 2;
            return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
        }
        int srcRectHeight = (int) (((float) srcWidth) / dstAspect);
        int scrRectTop = (srcHeight - srcRectHeight) / 2;
        return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
    }

    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        int width;
        int height;
        if (scalingLogic == ScalingLogic.FIT) {
            float srcAspect = ((float) srcWidth) / ((float) srcHeight);
            if (srcAspect > ((float) dstWidth) / ((float) dstHeight)) {
                width = dstWidth;
                height = (int) (((float) dstWidth) / srcAspect);
            } else {
                width = (int) (((float) dstHeight) * srcAspect);
                height = dstHeight;
            }
        } else {
            width = dstWidth;
            height = dstHeight;
        }
        return new Rect(0, 0, Math.max(width, 1), Math.max(height, 1));
    }
}
