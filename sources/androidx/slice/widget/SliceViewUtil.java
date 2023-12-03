package androidx.slice.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.R$attr;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.graphics.drawable.IconCompat;
import java.util.Calendar;

/* loaded from: classes.dex */
public class SliceViewUtil {
    public static void createCircledIcon(Context context, int iconSizePx, IconCompat icon, boolean isLarge, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(icon.loadDrawable(context));
        imageView.setScaleType(isLarge ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.CENTER_INSIDE);
        parent.addView(imageView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        if (isLarge) {
            Bitmap createBitmap = Bitmap.createBitmap(iconSizePx, iconSizePx, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            imageView.layout(0, 0, iconSizePx, iconSizePx);
            imageView.draw(canvas);
            imageView.setImageBitmap(getCircularBitmap(createBitmap));
        } else {
            imageView.setColorFilter(-1);
        }
        layoutParams.width = iconSizePx;
        layoutParams.height = iconSizePx;
        layoutParams.gravity = 17;
    }

    public static IconCompat createIconFromDrawable(Drawable d) {
        if (d instanceof BitmapDrawable) {
            return IconCompat.createWithBitmap(((BitmapDrawable) d).getBitmap());
        }
        Bitmap createBitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return IconCompat.createWithBitmap(createBitmap);
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    public static int getColorAccent(Context context) {
        return getColorAttr(context, 16843829);
    }

    public static int getColorAttr(Context context, int attr) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{attr});
        int color = obtainStyledAttributes.getColor(0, 0);
        obtainStyledAttributes.recycle();
        return color;
    }

    public static Drawable getDrawable(Context context, int attr) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{attr});
        Drawable drawable = obtainStyledAttributes.getDrawable(0);
        obtainStyledAttributes.recycle();
        return drawable;
    }

    public static CharSequence getTimestampString(Context context, long time) {
        return (time < System.currentTimeMillis() || DateUtils.isToday(time)) ? DateUtils.getRelativeTimeSpanString(time, Calendar.getInstance().getTimeInMillis(), 60000L, 262144) : DateUtils.formatDateTime(context, time, 8);
    }

    public static int resolveLayoutDirection(int layoutDir) {
        if (layoutDir == 2 || layoutDir == 3 || layoutDir == 1 || layoutDir == 0) {
            return layoutDir;
        }
        return -1;
    }

    public static void tintIndeterminateProgressBar(Context context, ProgressBar bar) {
        int colorAttr = getColorAttr(context, R$attr.colorControlHighlight);
        Drawable wrap = DrawableCompat.wrap(bar.getIndeterminateDrawable());
        if (wrap == null || colorAttr == 0) {
            return;
        }
        wrap.setColorFilter(colorAttr, PorterDuff.Mode.MULTIPLY);
        bar.setProgressDrawable(wrap);
    }
}
