package com.android.settings.datetime;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.datetime.DatePreferenceController;
import com.android.settings.datetime.TimePreferenceController;
import com.android.settings.report.InternationalCompat;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DateTimeSettings extends DashboardFragment implements TimePreferenceController.TimePreferenceHost, DatePreferenceController.DatePreferenceHost {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.date_time_prefs);
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.settings.datetime.DateTimeSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            FragmentActivity activity = DateTimeSettings.this.getActivity();
            if (activity != null) {
                DateTimeSettings.this.updateTimeAndDateDisplay(activity);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        FragmentActivity activity = getActivity();
        AutoTimeZonePreferenceController autoTimeZonePreferenceController = new AutoTimeZonePreferenceController(activity, this, activity.getIntent().getBooleanExtra("firstRun", false));
        AutoTimePreferenceController autoTimePreferenceController = new AutoTimePreferenceController(activity, this);
        arrayList.add(autoTimeZonePreferenceController);
        arrayList.add(autoTimePreferenceController);
        arrayList.add(new TimeZonePreferenceController(activity, autoTimeZonePreferenceController));
        arrayList.add(new TimePreferenceController(activity, this, autoTimePreferenceController));
        arrayList.add(new DatePreferenceController(activity, this, autoTimePreferenceController));
        arrayList.add(new AutoDualClockController(activity));
        arrayList.add(new DualClockZoneController(activity));
        arrayList.add(new DualClockHintController(activity));
        arrayList.add(new TimeFormatListPreferenceController(activity, this));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DateTimeSettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 38;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return DateTimeSettings.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.date_time_prefs;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        getSettingsLifecycle().addObserver(new TimeChangeListenerMixin(context, this));
        ((LocationTimeZoneDetectionPreferenceController) use(LocationTimeZoneDetectionPreferenceController.class)).setFragment(this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        InternationalCompat.trackReportEvent("setting_Additional_settings_time");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 0) {
            if (i == 1) {
                return ((TimePreferenceController) use(TimePreferenceController.class)).buildTimePicker(getActivity());
            }
            throw new IllegalArgumentException();
        }
        return ((DatePreferenceController) use(DatePreferenceController.class)).buildDatePicker(getActivity());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mIntentReceiver);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateActionBarTitleView(R.string.date_and_time_settings_title);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_TICK");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        getActivity().registerReceiver(this.mIntentReceiver, intentFilter, null, null);
    }

    @Override // com.android.settings.datetime.DatePreferenceController.DatePreferenceHost
    public void showDatePicker() {
        showDialog(0);
    }

    @Override // com.android.settings.datetime.TimePreferenceController.TimePreferenceHost
    public void showTimePicker() {
        removeDialog(1);
        showDialog(1);
    }

    @Override // com.android.settings.datetime.UpdateTimeAndDateCallback
    public void updateTimeAndDateDisplay(Context context) {
        lambda$onStart$0();
    }
}
