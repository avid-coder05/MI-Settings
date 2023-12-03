package com.miui.maml;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.miui.maml.MamlDrawable;

/* loaded from: classes2.dex */
public class AnimatingDrawable extends MamlDrawable {
    private String mClassName;
    private Context mContext;
    private FancyDrawable mFancyDrawable;
    private final Object mLock = new Object();
    private String mPackageName;
    private Drawable mQuietDrawable;
    private ResourceManager mResourceManager;
    private UserHandle mUser;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class AnimatingDrawableState extends MamlDrawable.MamlDrawableState {
        private String mClassName;
        private Context mContext;
        private String mPackageName;
        private ResourceManager mResourceManager;
        private UserHandle mUser;

        public AnimatingDrawableState(Context context, String str, String str2, ResourceManager resourceManager, UserHandle userHandle) {
            this.mContext = context;
            this.mResourceManager = resourceManager;
            this.mPackageName = str;
            this.mClassName = str2;
            this.mUser = userHandle;
        }

        @Override // com.miui.maml.MamlDrawable.MamlDrawableState
        protected MamlDrawable createDrawable() {
            return new AnimatingDrawable(this.mContext, this.mPackageName, this.mClassName, this.mResourceManager, this.mUser);
        }
    }

    public AnimatingDrawable(Context context, String str, String str2, ResourceManager resourceManager, UserHandle userHandle) {
        this.mContext = context;
        this.mResourceManager = resourceManager;
        this.mPackageName = str;
        this.mClassName = str2;
        this.mUser = userHandle;
        init();
    }

    private void init() {
        this.mState = new AnimatingDrawableState(this.mContext, this.mPackageName, this.mClassName, this.mResourceManager, this.mUser);
        Display defaultDisplay = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        this.mResourceManager.setExtraResource("den" + displayMetrics.densityDpi, displayMetrics.densityDpi);
        Drawable drawable = this.mResourceManager.getDrawable(this.mContext.getResources(), "quietImage.png");
        this.mQuietDrawable = drawable;
        if (drawable == null) {
            Log.e("Maml.AnimatingDrawable", "mQuietDrwable is null! package/class=" + this.mPackageName + "/" + this.mClassName);
            return;
        }
        setIntrinsicSize(drawable.getIntrinsicWidth(), this.mQuietDrawable.getIntrinsicHeight());
        Drawable mutate = this.mQuietDrawable.mutate();
        this.mQuietDrawable = mutate;
        mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), this.mQuietDrawable.getIntrinsicHeight());
        ColorFilter colorFilter = this.mColorFilter;
        if (colorFilter != null) {
            this.mQuietDrawable.setColorFilter(colorFilter);
        }
    }

    @Override // com.miui.maml.MamlDrawable
    protected void drawIcon(Canvas canvas) {
        try {
            int save = canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            canvas.scale(this.mWidth / this.mIntrinsicWidth, this.mHeight / this.mIntrinsicHeight, 0.0f, 0.0f);
            this.mQuietDrawable.draw(canvas);
            canvas.restoreToCount(save);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Maml.AnimatingDrawable", e.toString());
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            Log.e("Maml.AnimatingDrawable", e2.toString());
        }
    }

    @Override // com.miui.maml.MamlDrawable, android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        Drawable drawable = this.mQuietDrawable;
        if (drawable != null) {
            drawable.setAlpha(i);
        }
    }

    @Override // com.miui.maml.MamlDrawable, android.graphics.drawable.Drawable
    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
    }

    @Override // com.miui.maml.MamlDrawable, android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        Log.d("Maml.AnimatingDrawable", "setColorFilter");
        Drawable drawable = this.mQuietDrawable;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
        Drawable drawable2 = this.mBadgeDrawable;
        if (drawable2 != null) {
            drawable2.setColorFilter(colorFilter);
        }
        FancyDrawable fancyDrawable = this.mFancyDrawable;
        if (fancyDrawable != null) {
            fancyDrawable.setColorFilter(colorFilter);
        }
    }
}
