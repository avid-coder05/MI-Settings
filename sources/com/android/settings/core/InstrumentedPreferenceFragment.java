package com.android.settings.core;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.settings.MiuiFragmentThemeHandler;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.survey.SurveyMixin;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.instrumentation.VisibilityLoggerMixin;
import com.android.settingslib.core.lifecycle.ObservablePreferenceFragment;
import com.android.settingslib.miuisettings.preference.PreferenceUtils;

/* loaded from: classes.dex */
public abstract class InstrumentedPreferenceFragment extends ObservablePreferenceFragment implements Instrumentable {
    protected final int PLACEHOLDER_METRIC = 10000;
    protected MetricsFeatureProvider mMetricsFeatureProvider;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private MiuiFragmentThemeHandler mThemeHandler;
    private VisibilityLoggerMixin mVisibilityLoggerMixin;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class OnScrollListener extends RecyclerView.OnScrollListener {
        private final String mClassName;
        private final InteractionJankMonitor mMonitor;

        private OnScrollListener(String str) {
            this.mMonitor = InteractionJankMonitor.getInstance();
            this.mClassName = str;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 0) {
                this.mMonitor.end(28);
            } else if (i != 1) {
            } else {
                this.mMonitor.begin(recyclerView, 28);
            }
        }
    }

    private void updateActivityTitleWithScreenTitle(PreferenceScreen preferenceScreen) {
        if (preferenceScreen != null) {
            CharSequence title = preferenceScreen.getTitle();
            if (!TextUtils.isEmpty(title)) {
                getActivity().setTitle(title);
                return;
            }
            Log.w("InstrumentedPrefFrag", "Screen title missing for fragment " + getClass().getName());
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        updateActivityTitleWithScreenTitle(getPreferenceScreen());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Context getPrefContext() {
        return getPreferenceManager().getContext();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return -1;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mVisibilityLoggerMixin = new VisibilityLoggerMixin(getMetricsCategory(), this.mMetricsFeatureProvider);
        getSettingsLifecycle().addObserver(this.mVisibilityLoggerMixin);
        getSettingsLifecycle().addObserver(new SurveyMixin(this, getClass().getSimpleName()));
        super.onAttach(context);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        MiuiFragmentThemeHandler miuiFragmentThemeHandler = new MiuiFragmentThemeHandler(this);
        this.mThemeHandler = miuiFragmentThemeHandler;
        miuiFragmentThemeHandler.onCreate(bundle);
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        int preferenceScreenResId = getPreferenceScreenResId();
        if (preferenceScreenResId > 0) {
            addPreferencesFromResource(preferenceScreenResId);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean z = this.mThemeHandler.onOptionsItemSelected(menuItem) || super.onOptionsItemSelected(menuItem);
        if (z || menuItem.getIntent() == null) {
            return z;
        }
        getPrefContext().startActivity(menuItem.getIntent());
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        RecyclerView listView = getListView();
        RecyclerView.OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            listView.removeOnScrollListener(onScrollListener);
            this.mOnScrollListener = null;
        }
        super.onPause();
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        writePreferenceClickMetric(preference);
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        this.mVisibilityLoggerMixin.setSourceMetricsCategory(getActivity());
        RecyclerView listView = getListView();
        if (listView != null) {
            OnScrollListener onScrollListener = new OnScrollListener(getClass().getName());
            this.mOnScrollListener = onScrollListener;
            listView.addOnScrollListener(onScrollListener);
        }
        super.onResume();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mThemeHandler.onSaveInstanceState(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setVisible(Preference preference, boolean z) {
        PreferenceUtils.setVisible(preference, z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeElapsedTimeMetric(int i, String str) {
        this.mVisibilityLoggerMixin.writeElapsedTimeMetric(i, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writePreferenceClickMetric(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, getMetricsCategory());
    }
}
