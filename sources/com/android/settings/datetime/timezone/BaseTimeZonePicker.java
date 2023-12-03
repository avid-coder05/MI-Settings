package com.android.settings.datetime.timezone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.datetime.MiuiRegionEmptyViewObserver;
import com.android.settings.datetime.timezone.BaseTimeZoneAdapter;
import com.android.settings.datetime.timezone.model.TimeZoneData;
import com.android.settings.datetime.timezone.model.TimeZoneDataLoader;
import com.android.settings.utils.TabletUtils;
import com.google.android.material.appbar.AppBarLayout;
import java.util.Locale;

/* loaded from: classes.dex */
public abstract class BaseTimeZonePicker extends InstrumentedFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    private BaseTimeZoneAdapter mAdapter;
    protected MiuiRegionEmptyViewObserver mAdapterObserver;
    protected AppBarLayout mAppBarLayout;
    private final boolean mDefaultExpandSearch;
    protected RecyclerView mRecyclerView;
    private final boolean mSearchEnabled;
    private final int mSearchHintResId;
    private SearchView mSearchView;
    protected LinearLayout mSearchViewEmpty;
    private TimeZoneData mTimeZoneData;
    private final int mTitleResId;

    /* loaded from: classes.dex */
    public interface OnListItemClickListener<T extends BaseTimeZoneAdapter.AdapterItem> {
        void onListItemClick(T t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseTimeZonePicker(int i, int i2, boolean z, boolean z2) {
        this.mTitleResId = i;
        this.mSearchHintResId = i2;
        this.mSearchEnabled = z;
        this.mDefaultExpandSearch = z2;
    }

    private void disableToolBarScrollableBehavior() {
        AppBarLayout appBarLayout = this.mAppBarLayout;
        if (appBarLayout == null) {
            return;
        }
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() { // from class: com.android.settings.datetime.timezone.BaseTimeZonePicker.1
            @Override // com.google.android.material.appbar.AppBarLayout.BaseBehavior.BaseDragCallback
            public boolean canDrag(AppBarLayout appBarLayout2) {
                return false;
            }
        });
        layoutParams.setBehavior(behavior);
    }

    protected abstract BaseTimeZoneAdapter createAdapter(TimeZoneData timeZoneData);

    /* JADX INFO: Access modifiers changed from: protected */
    public void finish() {
        if (TabletUtils.IS_TABLET) {
            getFragmentManager().popBackStack();
        } else {
            getActivity().finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void finishAndSendResult(int i, Intent intent) {
        setResult(i, intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Locale getLocale() {
        return getContext().getResources().getConfiguration().getLocales().get(0);
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(this.mTitleResId);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (this.mSearchEnabled) {
            menuInflater.inflate(R.menu.time_zone_base_search_menu, menu);
            MenuItem findItem = menu.findItem(R.id.time_zone_search_menu);
            findItem.setOnActionExpandListener(this);
            SearchView searchView = (SearchView) findItem.getActionView();
            this.mSearchView = searchView;
            searchView.setQueryHint(getText(this.mSearchHintResId));
            this.mSearchView.setOnQueryTextListener(this);
            if (this.mDefaultExpandSearch) {
                findItem.expandActionView();
                this.mSearchView.setIconified(false);
                this.mSearchView.setActivated(true);
                this.mSearchView.setQuery("", true);
            }
            TextView textView = (TextView) this.mSearchView.findViewById(16909431);
            textView.setPadding(0, textView.getPaddingTop(), 0, textView.getPaddingBottom());
            View findViewById = this.mSearchView.findViewById(16909427);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
            layoutParams.setMarginStart(0);
            layoutParams.setMarginEnd(0);
            findViewById.setLayoutParams(layoutParams);
        }
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.time_zone_items_list, viewGroup, false);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.recycler_view);
        this.mRecyclerView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        this.mRecyclerView.setAdapter(this.mAdapter);
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.search_empty);
        this.mSearchViewEmpty = linearLayout;
        this.mAdapterObserver = new MiuiRegionEmptyViewObserver(linearLayout, this.mRecyclerView);
        this.mAppBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        disableToolBarScrollableBehavior();
        getLoaderManager().initLoader(0, null, new TimeZoneDataLoader.LoaderCreator(getContext(), new TimeZoneDataLoader.OnDataReadyCallback() { // from class: com.android.settings.datetime.timezone.BaseTimeZonePicker$$ExternalSyntheticLambda0
            @Override // com.android.settings.datetime.timezone.model.TimeZoneDataLoader.OnDataReadyCallback
            public final void onTimeZoneDataReady(TimeZoneData timeZoneData) {
                BaseTimeZonePicker.this.onTimeZoneDataReady(timeZoneData);
            }
        }));
        return inflate;
    }

    @Override // android.view.MenuItem.OnActionExpandListener
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        this.mAppBarLayout.setExpanded(false, false);
        ViewCompat.setNestedScrollingEnabled(this.mRecyclerView, true);
        return true;
    }

    @Override // android.view.MenuItem.OnActionExpandListener
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        this.mAppBarLayout.setExpanded(false, false);
        ViewCompat.setNestedScrollingEnabled(this.mRecyclerView, false);
        return true;
    }

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextChange(String str) {
        BaseTimeZoneAdapter baseTimeZoneAdapter = this.mAdapter;
        if (baseTimeZoneAdapter != null) {
            baseTimeZoneAdapter.getFilter().filter(str);
            return false;
        }
        return false;
    }

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextSubmit(String str) {
        return false;
    }

    public void onTimeZoneDataReady(TimeZoneData timeZoneData) {
        if (this.mTimeZoneData != null || timeZoneData == null) {
            return;
        }
        this.mTimeZoneData = timeZoneData;
        BaseTimeZoneAdapter createAdapter = createAdapter(timeZoneData);
        this.mAdapter = createAdapter;
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            recyclerView.setAdapter(createAdapter);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setResult(int i, Intent intent) {
        if (TabletUtils.IS_TABLET) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), i, intent);
        } else {
            getActivity().setResult(i, intent);
        }
    }
}
