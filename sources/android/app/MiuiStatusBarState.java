package android.app;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import com.miui.system.internal.R;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class MiuiStatusBarState {
    public static final int PROMPT_VERSION;
    private static final Method sSetDrawableTint;
    private Bundle mBundle;
    private RemoteViews mMiniStateViews;
    private int mPriority;
    private RemoteViews mStandardStateViews;
    private String mTag;

    /* loaded from: classes.dex */
    public static class MiniStateViewBuilder {
        private RemoteViews mViews;
        private boolean mIconShow = false;
        private boolean mTitleShow = false;
        private boolean mChronometerShow = false;

        public MiniStateViewBuilder(Context context) {
            this.mViews = new RemoteViews(context.getPackageName(), R.layout.miui_status_bar_mini_state_layout);
        }

        public RemoteViews build() {
            if (this.mIconShow && (this.mChronometerShow || this.mTitleShow)) {
                this.mViews.setViewVisibility(R.id.gap, 0);
            } else {
                this.mViews.setViewVisibility(R.id.gap, 8);
            }
            return this.mViews;
        }

        public MiniStateViewBuilder setBackgroundColor(int i) {
            try {
                MiuiStatusBarState.sSetDrawableTint.invoke(this.mViews, Integer.valueOf(R.id.app_info), Boolean.TRUE, Integer.valueOf(i), PorterDuff.Mode.SRC_IN);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public MiniStateViewBuilder setPendingIntent(PendingIntent pendingIntent) {
            this.mViews.setOnClickPendingIntent(R.id.app_info, pendingIntent);
            return this;
        }

        public MiniStateViewBuilder setTitle(String str) {
            this.mViews.setTextViewText(16908310, str);
            this.mViews.setViewVisibility(R.id.chronometer, 8);
            this.mViews.setViewVisibility(16908310, 0);
            this.mChronometerShow = false;
            this.mTitleShow = true;
            return this;
        }
    }

    static {
        PROMPT_VERSION = Build.VERSION.SDK_INT >= 30 ? 2 : 1;
        Method method = null;
        try {
            Class cls = Integer.TYPE;
            method = RemoteViews.class.getMethod("setDrawableTint", cls, Boolean.TYPE, cls, PorterDuff.Mode.class);
        } catch (Exception e) {
            Log.d("MiuiStatusBarState", e.toString());
        }
        sSetDrawableTint = method;
    }

    public MiuiStatusBarState(String str, RemoteViews remoteViews, RemoteViews remoteViews2, int i) {
        this.mTag = str;
        if (i > 3 || i < 0) {
            this.mPriority = 1;
        } else {
            this.mPriority = i;
        }
        this.mMiniStateViews = remoteViews2;
        this.mStandardStateViews = remoteViews;
    }

    public String getTag(Context context) {
        return context.getPackageName() + "." + this.mTag;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bundle toBundle(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString("key_status_bar_tag", this.mTag);
        bundle.putInt("key_status_bar_priority", this.mPriority);
        bundle.putString("key_status_bar_package_name", context.getPackageName());
        bundle.putParcelable("key_status_bar_mini_state", this.mMiniStateViews);
        bundle.putParcelable("key_status_bar_standard_state", this.mStandardStateViews);
        bundle.putBundle("extra", this.mBundle);
        return bundle;
    }
}
