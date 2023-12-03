package com.android.settings.datetime.timezone;

import android.content.Intent;
import android.icu.text.Collator;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.datetime.MiuiLocaleConverter;
import com.android.settings.datetime.timezone.BaseTimeZoneAdapter;
import com.android.settings.datetime.timezone.BaseTimeZonePicker;
import com.android.settings.datetime.timezone.RegionSearchPicker;
import com.android.settings.datetime.timezone.model.FilteredCountryTimeZones;
import com.android.settings.datetime.timezone.model.TimeZoneData;
import com.android.settings.utils.SettingsFeatures;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import miuix.appcompat.app.ActionBar;

/* loaded from: classes.dex */
public class RegionSearchPicker extends BaseTimeZonePicker {
    private ActionBar mActionBar;
    private BaseTimeZoneAdapter<RegionItem> mAdapter;
    private RecyclerView.OnScrollListener mOnListScrollListener;
    private String mSearchText;
    private EditText mSearchView;
    private TimeZoneData mTimeZoneData;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class RegionInfoComparator implements Comparator<RegionItem> {
        private final Collator mCollator;

        RegionInfoComparator(Collator collator) {
            this.mCollator = collator;
        }

        @Override // java.util.Comparator
        public int compare(RegionItem regionItem, RegionItem regionItem2) {
            return this.mCollator.compare(regionItem.getTitle(), regionItem2.getTitle());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class RegionItem implements BaseTimeZoneAdapter.AdapterItem {
        private final String mId;
        private final long mItemId;
        private final String mName;
        private final String[] mSearchKeys;

        RegionItem(long j, String str, String str2) {
            this.mId = str;
            this.mName = str2;
            this.mItemId = j;
            this.mSearchKeys = new String[]{str, str2};
        }

        @Override // com.android.settings.datetime.timezone.BaseTimeZoneAdapter.AdapterItem
        public String getCurrentTime() {
            return null;
        }

        @Override // com.android.settings.datetime.timezone.BaseTimeZoneAdapter.AdapterItem
        public String getIconText() {
            return null;
        }

        public String getId() {
            return this.mId;
        }

        @Override // com.android.settings.datetime.timezone.BaseTimeZoneAdapter.AdapterItem
        public long getItemId() {
            return this.mItemId;
        }

        @Override // com.android.settings.datetime.timezone.BaseTimeZoneAdapter.AdapterItem
        public String[] getSearchKeys() {
            return this.mSearchKeys;
        }

        @Override // com.android.settings.datetime.timezone.BaseTimeZoneAdapter.AdapterItem
        public CharSequence getSummary() {
            return null;
        }

        @Override // com.android.settings.datetime.timezone.BaseTimeZoneAdapter.AdapterItem
        public CharSequence getTitle() {
            return this.mName;
        }
    }

    public RegionSearchPicker() {
        super(R.string.date_time_select_region, R.string.date_time_search_region, true, true);
    }

    private List<RegionItem> createAdapterItem(Set<String> set) {
        TreeSet treeSet = new TreeSet(new RegionInfoComparator(Collator.getInstance(getLocale())));
        LocaleDisplayNames localeDisplayNames = LocaleDisplayNames.getInstance(getLocale());
        long j = 0;
        for (String str : set) {
            String convert = MiuiLocaleConverter.convert(localeDisplayNames.regionDisplayName(str));
            if (!TextUtils.isEmpty(convert)) {
                treeSet.add(new RegionItem(j, str, convert));
                j = 1 + j;
            }
        }
        return new ArrayList(treeSet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideSoftKeyboard() {
        if (getActivity() == null) {
            return;
        }
        ((InputMethodManager) getActivity().getSystemService("input_method")).hideSoftInputFromWindow(this.mRecyclerView.getWindowToken(), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onListItemClick(RegionItem regionItem) {
        String id = regionItem.getId();
        FilteredCountryTimeZones lookupCountryTimeZones = this.mTimeZoneData.lookupCountryTimeZones(id);
        getActivity();
        if (lookupCountryTimeZones == null || lookupCountryTimeZones.getPreferredTimeZoneIds().isEmpty()) {
            Log.e("RegionSearchPicker", "Region has no time zones: " + id);
            finishAndSendResult(0, null);
            return;
        }
        List<String> preferredTimeZoneIds = lookupCountryTimeZones.getPreferredTimeZoneIds();
        if (preferredTimeZoneIds.size() == 1) {
            finishAndSendResult(-1, new Intent().putExtra("com.android.settings.datetime.timezone.result_region_id", id).putExtra("com.android.settings.datetime.timezone.result_time_zone_id", preferredTimeZoneIds.get(0)));
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("com.android.settings.datetime.timezone.region_id", id);
        new SubSettingLauncher(getContext()).setDestination(RegionZonePicker.class.getCanonicalName()).setArguments(bundle).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 1).launch();
    }

    @Override // com.android.settings.datetime.timezone.BaseTimeZonePicker
    protected BaseTimeZoneAdapter createAdapter(TimeZoneData timeZoneData) {
        this.mTimeZoneData = timeZoneData;
        BaseTimeZoneAdapter<RegionItem> baseTimeZoneAdapter = new BaseTimeZoneAdapter<>(createAdapterItem(timeZoneData.getRegionIds()), new BaseTimeZonePicker.OnListItemClickListener() { // from class: com.android.settings.datetime.timezone.RegionSearchPicker$$ExternalSyntheticLambda0
            @Override // com.android.settings.datetime.timezone.BaseTimeZonePicker.OnListItemClickListener
            public final void onListItemClick(BaseTimeZoneAdapter.AdapterItem adapterItem) {
                RegionSearchPicker.this.onListItemClick((RegionSearchPicker.RegionItem) adapterItem);
            }
        }, getLocale(), false, null);
        this.mAdapter = baseTimeZoneAdapter;
        baseTimeZoneAdapter.registerAdapterDataObserver(this.mAdapterObserver);
        return this.mAdapter;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1355;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1) {
            if (i2 == -1) {
                setResult(-1, intent);
            }
            finish();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mAdapter.unregisterAdapterDataObserver(this.mAdapterObserver);
    }

    @Override // com.android.settings.datetime.timezone.BaseTimeZonePicker, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onInflateView = super.onInflateView(layoutInflater, viewGroup, bundle);
        ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
        this.mActionBar = appCompatActionBar;
        appCompatActionBar.setDisplayShowCustomEnabled(true);
        this.mActionBar.setDisplayShowTitleEnabled(false);
        this.mActionBar.setCustomView(R.layout.timezone_search_titlebar);
        View customView = this.mActionBar.getCustomView();
        EditText editText = (EditText) customView.findViewById(16908297);
        this.mSearchView = editText;
        editText.setContentDescription(getContext().getResources().getString(R.string.camera_key_action_shortcut_search));
        this.mSearchView.addTextChangedListener(new TextWatcher() { // from class: com.android.settings.datetime.timezone.RegionSearchPicker.1
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String charSequence2 = charSequence.toString();
                if (charSequence2.equals(RegionSearchPicker.this.mSearchText)) {
                    return;
                }
                RegionSearchPicker.this.mSearchText = charSequence2;
                RegionSearchPicker regionSearchPicker = RegionSearchPicker.this;
                regionSearchPicker.onQueryTextChange(regionSearchPicker.mSearchText);
                if (RegionSearchPicker.this.mSearchView != null) {
                    RegionSearchPicker.this.mSearchView.setContentDescription(TextUtils.isEmpty(RegionSearchPicker.this.mSearchText) ? RegionSearchPicker.this.getContext().getResources().getString(R.string.camera_key_action_shortcut_search) : RegionSearchPicker.this.mSearchText);
                }
            }
        });
        View findViewById = customView.findViewById(16908332);
        findViewById.setVisibility(0);
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.datetime.timezone.RegionSearchPicker.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RegionSearchPicker.this.finish();
            }
        });
        if (SettingsFeatures.isSplitTabletDevice()) {
            onInflateView.findViewById(R.id.springBackView).setPaddingRelative(getContext().getResources().getDimensionPixelOffset(R.dimen.locale_search_item_start_padding), 0, 0, 0);
        }
        return onInflateView;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() { // from class: com.android.settings.datetime.timezone.RegionSearchPicker.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                if (i == 1) {
                    RegionSearchPicker.this.hideSoftKeyboard();
                }
            }
        };
        this.mOnListScrollListener = onScrollListener;
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            recyclerView.addOnScrollListener(onScrollListener);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(this.mOnListScrollListener);
        }
    }
}
