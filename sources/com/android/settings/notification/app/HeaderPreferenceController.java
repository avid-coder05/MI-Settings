package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.widget.LayoutPreference;

/* loaded from: classes2.dex */
public class HeaderPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, LifecycleObserver {
    private final DashboardFragment mFragment;
    private EntityHeaderController mHeaderController;
    private boolean mStarted;

    public HeaderPreferenceController(Context context, DashboardFragment dashboardFragment) {
        super(context, null);
        this.mStarted = false;
        this.mFragment = dashboardFragment;
    }

    public CharSequence getLabel() {
        return (this.mChannel == null || isDefaultChannel()) ? this.mAppRow.label : this.mChannel.getName();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "pref_app_header";
    }

    public CharSequence getSecondSummary() {
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel == null) {
            return null;
        }
        return notificationChannel.getDescription();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mChannel != null) {
            NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
            if (notificationChannelGroup == null || TextUtils.isEmpty(notificationChannelGroup.getName())) {
                return this.mAppRow.label.toString();
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            BidiFormatter bidiFormatter = BidiFormatter.getInstance();
            spannableStringBuilder.append(bidiFormatter.unicodeWrap(this.mAppRow.label));
            spannableStringBuilder.append(bidiFormatter.unicodeWrap(((NotificationPreferenceController) this).mContext.getText(R.string.notification_header_divider_symbol_with_spaces)));
            spannableStringBuilder.append((CharSequence) bidiFormatter.unicodeWrap(this.mChannelGroup.getName().toString()));
            return spannableStringBuilder.toString();
        }
        return "";
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mAppRow != null;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mStarted = true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        DashboardFragment dashboardFragment;
        if (this.mAppRow == null || (dashboardFragment = this.mFragment) == null) {
            return;
        }
        FragmentActivity activity = this.mStarted ? dashboardFragment.getActivity() : null;
        if (activity == null) {
            return;
        }
        DashboardFragment dashboardFragment2 = this.mFragment;
        int i = R.id.entity_header;
        EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, dashboardFragment2, ((LayoutPreference) preference).findViewById(i));
        this.mHeaderController = newInstance;
        LayoutPreference done = newInstance.setIcon(this.mAppRow.icon).setLabel(getLabel()).setSummary(getSummary()).setSecondSummary(getSecondSummary()).setPackageName(this.mAppRow.pkg).setUid(this.mAppRow.uid).setButtonActions(1, 0).setHasAppInfoLink(true).setRecyclerView(this.mFragment.getListView(), this.mFragment.getSettingsLifecycle()).done(activity, ((NotificationPreferenceController) this).mContext);
        done.findViewById(i).setVisibility(0);
        done.findViewById(i).setBackground(null);
    }
}
