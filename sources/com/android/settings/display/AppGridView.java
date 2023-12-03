package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.IconDrawableFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import com.android.settings.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class AppGridView extends GridView {

    /* loaded from: classes.dex */
    public static class ActivityEntry implements Comparable<ActivityEntry> {
        public final ResolveInfo info;
        public final String label;
        private final IconDrawableFactory mIconFactory;
        private final int mUserId = UserHandle.myUserId();

        public ActivityEntry(ResolveInfo resolveInfo, String str, IconDrawableFactory iconDrawableFactory) {
            this.info = resolveInfo;
            this.label = str;
            this.mIconFactory = iconDrawableFactory;
        }

        @Override // java.lang.Comparable
        public int compareTo(ActivityEntry activityEntry) {
            return this.label.compareToIgnoreCase(activityEntry.label);
        }

        public Drawable getIcon() {
            IconDrawableFactory iconDrawableFactory = this.mIconFactory;
            ActivityInfo activityInfo = this.info.activityInfo;
            return iconDrawableFactory.getBadgedIcon(activityInfo, activityInfo.applicationInfo, this.mUserId);
        }

        public String toString() {
            return this.label;
        }
    }

    /* loaded from: classes.dex */
    public static class AppsAdapter extends ArrayAdapter<ActivityEntry> {
        private final int mIconResId;
        private final PackageManager mPackageManager;

        public AppsAdapter(Context context, int i, int i2, int i3) {
            super(context, i, i2);
            this.mIconResId = i3;
            this.mPackageManager = context.getPackageManager();
            loadAllApps();
        }

        private void loadAllApps() {
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            PackageManager packageManager = this.mPackageManager;
            ArrayList arrayList = new ArrayList();
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
            IconDrawableFactory newInstance = IconDrawableFactory.newInstance(getContext());
            for (ResolveInfo resolveInfo : queryIntentActivities) {
                CharSequence loadLabel = resolveInfo.loadLabel(packageManager);
                if (loadLabel != null) {
                    arrayList.add(new ActivityEntry(resolveInfo, loadLabel.toString(), newInstance));
                }
                if (arrayList.size() >= 6) {
                    break;
                }
            }
            Collections.sort(arrayList);
            addAll(arrayList);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            ((ImageView) view2.findViewById(this.mIconResId)).setImageDrawable(getItem(i).getIcon());
            return view2;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int i) {
            return false;
        }
    }

    public AppGridView(Context context) {
        super(context);
        init(context);
    }

    public AppGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public AppGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public AppGridView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context);
    }

    private void init(Context context) {
        setAdapter((ListAdapter) new AppsAdapter(context, R.layout.screen_zoom_preview_app_icon, 16908308, 16908295));
    }
}
