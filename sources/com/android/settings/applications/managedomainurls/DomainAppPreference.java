package com.android.settings.applications.managedomainurls;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.miuisettings.preference.IconPreference;

/* loaded from: classes.dex */
public class DomainAppPreference extends IconPreference {
    private ApplicationsState.AppEntry mEntry;
    private IconDrawableFactory mIconDrawableFactory;
    private PackageManager mPm;

    public DomainAppPreference(Context context, IconDrawableFactory iconDrawableFactory, ApplicationsState.AppEntry appEntry, boolean z) {
        super(context, z);
        init(context, iconDrawableFactory, appEntry);
    }

    private CharSequence getDomainsSummary(String str) {
        if (this.mPm.getIntentVerificationStatusAsUser(str, UserHandle.myUserId()) == 3) {
            return getContext().getText(R.string.domain_urls_summary_none);
        }
        ArraySet<String> handledDomains = Utils.getHandledDomains(this.mPm, str);
        return handledDomains.isEmpty() ? getContext().getText(R.string.domain_urls_summary_none) : handledDomains.size() == 1 ? getContext().getString(R.string.domain_urls_summary_one, handledDomains.valueAt(0)) : getContext().getString(R.string.domain_urls_summary_some, handledDomains.valueAt(0));
    }

    private void init(Context context, IconDrawableFactory iconDrawableFactory, ApplicationsState.AppEntry appEntry) {
        this.mIconDrawableFactory = iconDrawableFactory;
        this.mPm = context.getPackageManager();
        this.mEntry = appEntry;
        appEntry.ensureLabel(getContext());
        setState();
    }

    private void setState() {
        setTitle(this.mEntry.label);
        setIcon(this.mIconDrawableFactory.getBadgedIcon(this.mEntry.info));
        setSummary(getDomainsSummary(this.mEntry.info.packageName));
    }

    public ApplicationsState.AppEntry getEntry() {
        return this.mEntry;
    }

    public void reuse() {
        setState();
        notifyChanged();
    }
}
