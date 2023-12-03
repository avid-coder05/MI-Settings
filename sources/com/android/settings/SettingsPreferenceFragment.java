package com.android.settings;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.CustomListPreference;
import com.android.settings.RestrictedListPreference;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.recommend.RecommendFilter;
import com.android.settings.recommend.RecommendManager;
import com.android.settings.recommend.RecommendUIHelper;
import com.android.settings.recommend.bean.RecommendItem;
import com.android.settings.support.actionbar.HelpResourceProvider;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.widget.HighlightablePreferenceGroupAdapter;
import com.android.settings.widget.LoadingViewController;
import com.android.settingslib.CustomDialogPreference;
import com.android.settingslib.CustomDialogPreferenceCompat;
import com.android.settingslib.CustomEditTextPreference;
import com.android.settingslib.CustomEditTextPreferenceCompat;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import com.android.settingslib.miuisettings.preference.PreferenceUtils;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.android.settingslib.widget.LayoutPreference;
import com.google.android.material.appbar.AppBarLayout;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import miuix.appcompat.app.ActionBar;

/* loaded from: classes.dex */
public abstract class SettingsPreferenceFragment extends InstrumentedPreferenceFragment implements DialogCreatable, HelpResourceProvider {
    private RecommendUIHelper UIHelper;
    protected boolean delayToBuildRecommendLayout;
    private boolean mAnimationAllowed;
    private AppBarLayout mAppBarLayout;
    private ContentResolver mContentResolver;
    private RecyclerView.Adapter mCurrentRootAdapter;
    private SettingsDialogFragment mDialogFragment;
    private View mEmptyView;
    private LayoutPreference mHeader;
    private LinearLayoutManager mLayoutManager;
    protected ViewGroup mPinnedHeaderFrameLayout;
    private ArrayMap<String, Preference> mPreferenceCache;
    private boolean mIsDataSetObserverRegistered = false;
    private RecyclerView.AdapterDataObserver mDataSetObserver = new RecyclerView.AdapterDataObserver() { // from class: com.android.settings.SettingsPreferenceFragment.1
        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2, Object obj) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeInserted(int i, int i2) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeMoved(int i, int i2, int i3) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeRemoved(int i, int i2) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }
    };
    public boolean mPreferenceHighlighted = false;

    /* loaded from: classes.dex */
    public static class SettingsDialogFragment extends InstrumentedDialogFragment {
        public DialogInterface.OnCancelListener mOnCancelListener;
        public DialogInterface.OnDismissListener mOnDismissListener;
        private Fragment mParentFragment;

        public static SettingsDialogFragment newInstance(DialogCreatable dialogCreatable, int i) {
            if (dialogCreatable instanceof Fragment) {
                SettingsDialogFragment settingsDialogFragment = new SettingsDialogFragment();
                settingsDialogFragment.setParentFragment(dialogCreatable);
                settingsDialogFragment.setDialogId(i);
                return settingsDialogFragment;
            }
            throw new IllegalArgumentException("fragment argument must be an instance of " + Fragment.class.getName());
        }

        private void setDialogId(int i) {
            this.mDialogId = i;
        }

        private void setParentFragment(DialogCreatable dialogCreatable) {
            this.mParentFragment = (Fragment) dialogCreatable;
        }

        public int getDialogId() {
            return this.mDialogId;
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 0;
        }

        @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialogInterface) {
            super.onCancel(dialogInterface);
            DialogInterface.OnCancelListener onCancelListener = this.mOnCancelListener;
            if (onCancelListener != null) {
                onCancelListener.onCancel(dialogInterface);
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            if (bundle != null) {
                this.mDialogId = bundle.getInt("key_dialog_id", 0);
                this.mParentFragment = getParentFragment();
                int i = bundle.getInt("key_parent_fragment_id", -1);
                if (this.mParentFragment == null) {
                    this.mParentFragment = getFragmentManager().findFragmentById(i);
                }
                Fragment fragment = this.mParentFragment;
                if (!(fragment instanceof DialogCreatable)) {
                    StringBuilder sb = new StringBuilder();
                    Fragment fragment2 = this.mParentFragment;
                    sb.append(fragment2 != null ? fragment2.getClass().getName() : Integer.valueOf(i));
                    sb.append(" must implement ");
                    sb.append(DialogCreatable.class.getName());
                    throw new IllegalArgumentException(sb.toString());
                } else if (fragment instanceof SettingsPreferenceFragment) {
                    ((SettingsPreferenceFragment) fragment).mDialogFragment = this;
                }
            }
            Dialog onCreateDialog = ((DialogCreatable) this.mParentFragment).onCreateDialog(this.mDialogId);
            if (onCreateDialog == null) {
                setShowsDialog(false);
            }
            return onCreateDialog;
        }

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onDetach() {
            super.onDetach();
            Fragment fragment = this.mParentFragment;
            if ((fragment instanceof SettingsPreferenceFragment) && ((SettingsPreferenceFragment) fragment).mDialogFragment == this) {
                ((SettingsPreferenceFragment) this.mParentFragment).mDialogFragment = null;
            }
        }

        @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) {
            super.onDismiss(dialogInterface);
            DialogInterface.OnDismissListener onDismissListener = this.mOnDismissListener;
            if (onDismissListener != null) {
                onDismissListener.onDismiss(dialogInterface);
            }
        }

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            if (this.mParentFragment != null) {
                bundle.putInt("key_dialog_id", this.mDialogId);
                bundle.putInt("key_parent_fragment_id", this.mParentFragment.getId());
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onStart() {
            super.onStart();
            Fragment fragment = this.mParentFragment;
            if (fragment == null || !(fragment instanceof SettingsPreferenceFragment)) {
                return;
            }
            ((SettingsPreferenceFragment) fragment).onDialogShowing();
        }
    }

    private void addPreferenceToTop(LayoutPreference layoutPreference) {
        layoutPreference.setOrder(-1);
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().addPreference(layoutPreference);
        }
    }

    private void setupActionBar() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            int i = arguments.getInt(":android:show_fragment_title");
            ActionBar appCompatActionBar = getAppCompatActionBar();
            CharSequence charSequence = arguments.getCharSequence(":settings:show_fragment_title");
            if (appCompatActionBar != null) {
                if (i > 0) {
                    appCompatActionBar.setTitle(i);
                } else if (TextUtils.isEmpty(charSequence)) {
                } else {
                    appCompatActionBar.setTitle(charSequence);
                }
            }
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        checkAvailablePrefs(getPreferenceScreen());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cacheRemoveAllPrefs(PreferenceGroup preferenceGroup) {
        this.mPreferenceCache = new ArrayMap<>();
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (!TextUtils.isEmpty(preference.getKey())) {
                this.mPreferenceCache.put(preference.getKey(), preference);
            }
        }
    }

    void checkAvailablePrefs(PreferenceGroup preferenceGroup) {
        if (preferenceGroup == null) {
            return;
        }
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if ((preference instanceof SelfAvailablePreference) && !((SelfAvailablePreference) preference).isAvailable(getContext())) {
                PreferenceUtils.setVisible(preference, false);
            } else if (preference instanceof PreferenceGroup) {
                checkAvailablePrefs((PreferenceGroup) preference);
            }
        }
    }

    public void finish() {
        if (getActivity() == null) {
            return;
        }
        if (!(getActivity() instanceof MiuiSettings)) {
            if (isResumed()) {
                getActivity().onBackPressed();
                return;
            } else {
                getActivity().finish();
                return;
            }
        }
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null || !isResumed()) {
            getActivity().getFragmentManager().popBackStack();
        } else {
            fragmentManager.popBackStack();
        }
    }

    public final void finishFragment() {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Preference getCachedPreference(String str) {
        ArrayMap<String, Preference> arrayMap = this.mPreferenceCache;
        if (arrayMap != null) {
            return arrayMap.remove(str);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ContentResolver getContentResolver() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            this.mContentResolver = activity.getContentResolver();
        }
        return this.mContentResolver;
    }

    public View getEmptyView() {
        return this.mEmptyView;
    }

    public LayoutPreference getHeaderView() {
        return this.mHeader;
    }

    public int getInitialExpandedChildCount() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Intent getIntent() {
        if (getActivity() == null) {
            return null;
        }
        return getActivity().getIntent();
    }

    public int getMetricsCategory() {
        return 0;
    }

    public String getName() {
        return "SettingsPreference";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Button getNextButton() {
        return ((ButtonBarHandler) getActivity()).getNextButton();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PackageManager getPackageManager() {
        return getActivity().getPackageManager();
    }

    public int getPageIndex() {
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T> T getSystemService(Class<T> cls) {
        return (T) getActivity().getSystemService(cls);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object getSystemService(String str) {
        return getActivity().getSystemService(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasNextButton() {
        return ((ButtonBarHandler) getActivity()).hasNextButton();
    }

    public void highlightPreferenceIfNeeded() {
        isAdded();
    }

    public boolean isDialogShowing(int i) {
        Dialog dialog;
        SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment == null || settingsDialogFragment.getDialogId() != i || (dialog = this.mDialogFragment.getDialog()) == null) {
            return false;
        }
        return dialog.isShowing();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isFinishingOrDestroyed() {
        FragmentActivity activity = getActivity();
        return activity == null || activity.isFinishing() || activity.isDestroyed();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isMiuiSettingsActivity() {
        return getActivity() instanceof MiuiSettings;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isPreferenceExpanded(Preference preference) {
        return false;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onBindPreferences() {
        super.onBindPreferences();
        registerObserverIfNeeded();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mPreferenceHighlighted = bundle.getBoolean("android:preference_highlighted");
        }
        HighlightablePreferenceGroupAdapter.adjustInitialExpandedChildCount(this);
        ActionBar appCompatActionBar = getAppCompatActionBar();
        if (appCompatActionBar == null || !SettingsFeatures.isSplitTablet(getContext())) {
            return;
        }
        appCompatActionBar.setExpandState(0);
        appCompatActionBar.setResizable(false);
    }

    public Dialog onCreateDialog(int i) {
        return null;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView.LayoutManager onCreateLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        this.mLayoutManager = linearLayoutManager;
        return linearLayoutManager;
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (viewGroup != null) {
            MiuiUtils.updateFragmentView(getActivity(), viewGroup);
        }
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mPinnedHeaderFrameLayout = (ViewGroup) onCreateView.findViewById(R.id.pinned_header);
        this.mAppBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        getListView().setItemAnimator(null);
        return onCreateView;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDataSetChanged() {
        highlightPreferenceIfNeeded();
        updateEmptyView();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        SettingsDialogFragment settingsDialogFragment;
        if (isRemoving() && (settingsDialogFragment = this.mDialogFragment) != null) {
            settingsDialogFragment.dismiss();
            this.mDialogFragment = null;
        }
        super.onDetach();
    }

    public void onDialogShowing() {
    }

    @Override // miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnDisplayPreferenceDialogListener
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment newInstance;
        if (preference.getKey() == null) {
            preference.setKey(UUID.randomUUID().toString());
        }
        if (preference instanceof RestrictedListPreference) {
            newInstance = RestrictedListPreference.RestrictedListPreferenceDialogFragment.newInstance(preference.getKey());
        } else if (preference instanceof CustomListPreference) {
            newInstance = CustomListPreference.CustomListPreferenceDialogFragment.newInstance(preference.getKey());
        } else if (preference instanceof CustomDialogPreferenceCompat) {
            newInstance = CustomDialogPreferenceCompat.CustomPreferenceDialogFragment.newInstance(preference.getKey());
        } else if (preference instanceof CustomEditTextPreferenceCompat) {
            newInstance = CustomEditTextPreferenceCompat.CustomPreferenceDialogFragment.newInstance(preference.getKey());
        } else if (preference instanceof CustomDialogPreference) {
            newInstance = CustomDialogPreference.CustomPreferenceDialogFragment.newInstance(preference.getKey());
        } else if (!(preference instanceof CustomEditTextPreference)) {
            super.onDisplayPreferenceDialog(preference);
            return;
        } else {
            newInstance = CustomEditTextPreference.CustomPreferenceDialogFragment.newInstance(preference.getKey());
        }
        newInstance.setTargetFragment(this, 0);
        newInstance.show(getFragmentManager(), "dialog_preference");
        onDialogShowing();
    }

    public void onFragmentResult(int i, Bundle bundle) {
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (getName() == null || TextUtils.equals("SettingsPreference", getName())) {
            return;
        }
        try {
            MiStatInterfaceUtils.trackPageEnd(getName());
        } catch (IllegalStateException unused) {
            Log.d("SettingsPreference", "IllegalStateException occurs in SettingsPreferenceFragment " + getName());
        }
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (isAdded()) {
            if (TextUtils.isEmpty(preference.getKey())) {
                String resourceName = MiuiUtils.getResourceName(getActivity(), preference.getTitle());
                MiStatInterfaceUtils.trackPreferenceClick(getName(), resourceName);
                OneTrackInterfaceUtils.trackPreferenceClick(getName(), resourceName);
            } else {
                MiStatInterfaceUtils.trackPreferenceClick(getName(), preference.getKey());
                OneTrackInterfaceUtils.trackPreferenceClick(getName(), preference.getKey());
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        if (menu.size() > 0) {
            MiuiUtils.setNavigationBackground(getActivity(), false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        highlightPreferenceIfNeeded();
        if (getName() == null || TextUtils.equals("SettingsPreference", getName())) {
            return;
        }
        try {
            MiStatInterfaceUtils.trackPageStart(getName());
        } catch (IllegalStateException unused) {
            Log.d("SettingsPreference", "IllegalStateException occurs in SettingsPreferenceFragment " + getName());
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("android:preference_highlighted", this.mPreferenceHighlighted);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        setupActionBar();
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    protected void onUnbindPreferences() {
        super.onUnbindPreferences();
        unregisterObserverIfNeeded();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        if (this.delayToBuildRecommendLayout) {
            Log.e("SettingsPreference", "No need to build because delayToBuildRecommendLayout = true");
        } else {
            tryBuildRecommendLayout(0, false);
        }
        super.onViewCreated(view, bundle);
    }

    public void registerObserverIfNeeded() {
        if (this.mIsDataSetObserverRegistered) {
            return;
        }
        RecyclerView.Adapter adapter = this.mCurrentRootAdapter;
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(this.mDataSetObserver);
        }
        RecyclerView.Adapter adapter2 = getListView().getAdapter();
        this.mCurrentRootAdapter = adapter2;
        if (adapter2 != null) {
            adapter2.registerAdapterDataObserver(this.mDataSetObserver);
        }
        this.mIsDataSetObserverRegistered = true;
        onDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeCachedPrefs(PreferenceGroup preferenceGroup) {
        Iterator<Preference> it = this.mPreferenceCache.values().iterator();
        while (it.hasNext()) {
            preferenceGroup.removePreference(it.next());
        }
        this.mPreferenceCache = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeDialog(int i) {
        SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment != null && settingsDialogFragment.getDialogId() == i) {
            this.mDialogFragment.dismissAllowingStateLoss();
        }
        this.mDialogFragment = null;
    }

    boolean removePreference(PreferenceGroup preferenceGroup, String str) {
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (TextUtils.equals(preference.getKey(), str)) {
                return preferenceGroup.removePreference(preference);
            }
            if ((preference instanceof PreferenceGroup) && removePreference((PreferenceGroup) preference, str)) {
                return true;
            }
        }
        return false;
    }

    public boolean removePreference(String str) {
        return removePreference(getPreferenceScreen(), str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setAnimationAllowed(boolean z) {
        this.mAnimationAllowed = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setCancelable(boolean z) {
        SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment != null) {
            settingsDialogFragment.setCancelable(z);
        }
    }

    public void setEmptyView(View view) {
        View view2 = this.mEmptyView;
        if (view2 != null) {
            view2.setVisibility(8);
        }
        this.mEmptyView = view;
        updateEmptyView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setHeaderView(int i) {
        LayoutPreference layoutPreference = new LayoutPreference(getPrefContext(), i);
        this.mHeader = layoutPreference;
        layoutPreference.setSelectable(false);
        addPreferenceToTop(this.mHeader);
    }

    public void setLoading(boolean z, boolean z2) {
        LoadingViewController.handleLoadingContainer(getView().findViewById(R.id.loading_container), getListView(), !z, z2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment != null) {
            settingsDialogFragment.mOnCancelListener = onCancelListener;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment != null) {
            settingsDialogFragment.mOnDismissListener = onDismissListener;
        }
    }

    public View setPinnedHeaderView(int i) {
        View inflate = getActivity().getLayoutInflater().inflate(i, this.mPinnedHeaderFrameLayout, false);
        setPinnedHeaderView(inflate);
        return inflate;
    }

    public void setPinnedHeaderView(View view) {
        ViewGroup viewGroup = this.mPinnedHeaderFrameLayout;
        if (viewGroup != null) {
            viewGroup.addView(view);
            this.mPinnedHeaderFrameLayout.setVisibility(0);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference;
        super.setPreferenceScreen(preferenceScreen);
        if (preferenceScreen == null || (layoutPreference = this.mHeader) == null) {
            return;
        }
        preferenceScreen.addPreference(layoutPreference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setResult(int i) {
        if (getActivity() == null) {
            return;
        }
        getActivity().setResult(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setResult(int i, Intent intent) {
        if (getActivity() == null) {
            return;
        }
        getActivity().setResult(i, intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showDialog(int i) {
        if (this.mDialogFragment != null) {
            Log.e("SettingsPreference", "Old dialog fragment not null!");
        }
        SettingsDialogFragment newInstance = SettingsDialogFragment.newInstance(this, i);
        this.mDialogFragment = newInstance;
        newInstance.show(getChildFragmentManager(), Integer.toString(i));
    }

    public void showPinnedHeader(boolean z) {
        ViewGroup viewGroup = this.mPinnedHeaderFrameLayout;
        if (viewGroup == null) {
            return;
        }
        viewGroup.setVisibility(z ? 0 : 4);
    }

    public boolean startFragment(Fragment fragment, String str, int i, int i2, Bundle bundle) {
        FragmentActivity activity = getActivity();
        if (activity instanceof MiuiSettings) {
            ((MiuiSettings) activity).startPreferencePanel(str, bundle, i, null, fragment, i2);
            return true;
        } else if (activity instanceof PreferenceActivity) {
            ((PreferenceActivity) activity).startPreferencePanel(str, bundle, i, null, fragment, i2);
            return true;
        } else {
            new SubSettingLauncher(getContext()).setDestination(str).setTitleRes(i).setArguments(bundle).setResultListener(fragment, i2).launch();
            return true;
        }
    }

    public boolean startFragment(Fragment fragment, String str, int i, Bundle bundle) {
        return startFragment(fragment, str, R.string.lock_settings_title, i, bundle);
    }

    public boolean startFragment(Fragment fragment, String str, int i, Bundle bundle, int i2) {
        return startFragment(fragment, str, i2, i, bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void tryBuildRecommendLayout(int i, boolean z) {
        if (!(UserHandle.myUserId() == 0) || !RecommendManager.isLoadComplete()) {
            Log.e("SettingsPreference", "recommend items not load complete.");
            return;
        }
        List<RecommendItem> listByPageIndex = new RecommendFilter().getListByPageIndex(getActivity().getApplicationContext(), getPageIndex());
        if (this.UIHelper == null) {
            this.UIHelper = new RecommendUIHelper(this);
        }
        this.UIHelper.buildRecommendLayout(listByPageIndex, i, z);
    }

    public void unregisterObserverIfNeeded() {
        if (this.mIsDataSetObserverRegistered) {
            RecyclerView.Adapter adapter = this.mCurrentRootAdapter;
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(this.mDataSetObserver);
                this.mCurrentRootAdapter = null;
            }
            this.mIsDataSetObserverRegistered = false;
        }
    }

    void updateEmptyView() {
        if (this.mEmptyView == null) {
            return;
        }
        if (getPreferenceScreen() == null) {
            this.mEmptyView.setVisibility(0);
            return;
        }
        View findViewById = getActivity().findViewById(16908351);
        boolean z = true;
        if (getPreferenceScreen().getPreferenceCount() - (this.mHeader != null ? 1 : 0) > 0 && (findViewById == null || findViewById.getVisibility() == 0)) {
            z = false;
        }
        this.mEmptyView.setVisibility(z ? 0 : 8);
    }
}
