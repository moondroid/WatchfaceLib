package it.moondroid.watchfacelib.ui;

/**
 * Created by marco.granatiero on 22/01/2015.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;

public class ScaledDrawable {

    private static final String TAG;
    private static ScaledDrawable instance;
    private final LruCache<ColoredDrawableInfo, Drawable> mColoredDrawableCache;
    private Context mContext;
    private float mScale;

    private static class ColoredDrawableInfo {
        Integer appliedColor;
        int drawableId;

        public ColoredDrawableInfo(int drawableId, Integer color) {
            this.drawableId = drawableId;
            this.appliedColor = color;
        }

        public int hashCode() {
            return (((this.appliedColor == null ? 0 : this.appliedColor.hashCode()) + 31) * 31) + this.drawableId;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ColoredDrawableInfo other = (ColoredDrawableInfo) obj;
            if (this.appliedColor == null) {
                if (other.appliedColor != null) {
                    return false;
                }
            } else if (!this.appliedColor.equals(other.appliedColor)) {
                return false;
            }
            return this.drawableId == other.drawableId;
        }

        public String toString() {
            return new StringBuilder("ColoredDrawableInfo [drawableId=").append(this.drawableId).append(", appliedColor=").append(this.appliedColor).append("]").toString();
        }
    }

    static {
        TAG = ScaledDrawable.class.getSimpleName();
    }

    public static ScaledDrawable getInstance(Context context) {
        if (instance == null) {
            synchronized (ScaledDrawable.class) {
                if (instance == null) {
                    instance = new ScaledDrawable(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public ScaledDrawable(Context context) {
        this.mColoredDrawableCache = new LruCache(Integer.MAX_VALUE);
        this.mContext = context;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Log.i(TAG, String.format("Screen sizes: w: %d, h: %d", new Object[]{Integer.valueOf(dm.widthPixels), Integer.valueOf(dm.heightPixels)}));
        this.mScale = ((float) Math.max(dm.widthPixels, dm.heightPixels)) / 320.0f;
    }

    public Drawable getScaledDrawable(int drawableRes) {
        ColoredDrawableInfo key = new ColoredDrawableInfo(drawableRes, null);
        Drawable drawable = (Drawable) this.mColoredDrawableCache.get(key);
        if (drawable == null) {
            drawable = Scale.scaleDrawable(drawableRes, this.mContext, this.mScale, true);
            if (drawable != null) {
                this.mColoredDrawableCache.put(key, drawable);
            }
        }
        return drawable;
    }

    public Drawable getScaledDrawableWithColor(int drawableRes, int color) {
        ColoredDrawableInfo key = new ColoredDrawableInfo(drawableRes, Integer.valueOf(color));
        Drawable drawable = (Drawable) this.mColoredDrawableCache.get(key);
        if (drawable == null) {
            Log.i(TAG, new StringBuilder("Couldn't find key: ").append(key).toString());
            drawable = Scale.scaleDrawable(drawableRes, this.mContext, this.mScale, true);
            if (drawable == null) {
                return drawable;
            }
            drawable = applyDrawableColor(drawable, color);
            this.mColoredDrawableCache.put(key, drawable);
            return drawable;
        }
        Log.i(TAG, new StringBuilder("Found key: ").append(key).toString());
        return drawable;
    }

    public Bitmap scaleBitmap(Bitmap bitmap, boolean recycle) {
        return Scale.scaleBitmap(bitmap, this.mContext, this.mScale, recycle);
    }

    protected Drawable applyDrawableColor(Drawable drawable, int color) {
        int red = (16711680 & color) / 65535;
        int green = (65280 & color) / 255;
        int blue = color & 255;
        drawable.setColorFilter(new ColorMatrixColorFilter(new float[]{0.0f, 0.0f, 0.0f, 0.0f, (float) red, 0.0f, 0.0f, 0.0f, 0.0f, (float) green, 0.0f, 0.0f, 0.0f, 0.0f, (float) blue, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        return drawable;
    }

    public int getScaledDimen(int px) {
        return (int) (this.mScale * ((float) px));
    }

    public float getScale() {
        return this.mScale;
    }
}
