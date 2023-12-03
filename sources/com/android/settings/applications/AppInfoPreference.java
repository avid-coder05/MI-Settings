package com.android.settings.applications;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class AppInfoPreference extends Preference {
    private AppInfo mAppInfo;
    private TextView mAppVersion;
    private ImageView mIcon;
    private TextView mLabel;

    /* loaded from: classes.dex */
    public static class AppInfo {
        public String mAppVersion;
        public boolean mAppVersionVisible;
        public Drawable mIcon;
        public String mLabel;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AppInfo(Drawable drawable, String str, String str2, boolean z) {
            this.mIcon = drawable;
            this.mLabel = str;
            this.mAppVersion = str2;
            this.mAppVersionVisible = z;
        }
    }

    public AppInfoPreference(Context context) {
        super(context);
    }

    public AppInfoPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AppInfoPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private void updateUi() {
        AppInfo appInfo = this.mAppInfo;
        if (appInfo != null) {
            ImageView imageView = this.mIcon;
            if (imageView != null) {
                imageView.setImageDrawable(appInfo.mIcon);
            }
            TextView textView = this.mLabel;
            if (textView != null) {
                textView.setText(this.mAppInfo.mLabel);
            }
            TextView textView2 = this.mAppVersion;
            if (textView2 != null) {
                AppInfo appInfo2 = this.mAppInfo;
                if (!appInfo2.mAppVersionVisible) {
                    textView2.setVisibility(4);
                    return;
                }
                textView2.setText(appInfo2.mAppVersion);
                this.mAppVersion.setVisibility(0);
            }
        }
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        this.mIcon = (ImageView) view.findViewById(R.id.app_icon);
        this.mLabel = (TextView) view.findViewById(R.id.app_name);
        this.mAppVersion = (TextView) view.findViewById(R.id.app_size);
        view.setBackground(null);
        updateUi();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        setLayoutResource(R.layout.manage_applications_item);
        return null;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.mAppInfo = appInfo;
        updateUi();
    }
}
