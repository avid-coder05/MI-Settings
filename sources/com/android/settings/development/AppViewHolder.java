package com.android.settings.development;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settingslib.applications.ApplicationsState;

/* loaded from: classes.dex */
public class AppViewHolder {
    public ImageView appIcon;
    public TextView appName;
    public TextView disabled;
    public ApplicationsState.AppEntry entry;
    public View rootView;
    public TextView summary;
    public View widget;

    public static AppViewHolder createOrRecycle(LayoutInflater layoutInflater, View view) {
        if (view == null) {
            View inflate = layoutInflater.inflate(R.layout.preference_app, (ViewGroup) null);
            AppViewHolder appViewHolder = new AppViewHolder();
            appViewHolder.rootView = inflate;
            appViewHolder.appName = (TextView) inflate.findViewById(16908310);
            appViewHolder.appIcon = (ImageView) inflate.findViewById(16908294);
            appViewHolder.summary = (TextView) inflate.findViewById(16908304);
            appViewHolder.disabled = (TextView) inflate.findViewById(R.id.appendix);
            appViewHolder.widget = inflate.findViewById(16908312);
            inflate.setTag(appViewHolder);
            return appViewHolder;
        }
        return (AppViewHolder) view.getTag();
    }

    public void updateSizeText(CharSequence charSequence, int i) {
        ApplicationsState.AppEntry appEntry = this.entry;
        String str = appEntry.sizeStr;
        if (str == null) {
            if (appEntry.size == -2) {
                this.summary.setText(charSequence);
            }
        } else if (i == 1) {
            this.summary.setText(appEntry.internalSizeStr);
        } else if (i != 2) {
            this.summary.setText(str);
        } else {
            this.summary.setText(appEntry.externalSizeStr);
        }
    }
}
