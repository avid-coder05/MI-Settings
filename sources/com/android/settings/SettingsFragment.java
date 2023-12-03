package com.android.settings;

import android.app.ActivityManagerNative;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.settings.suggestions.Suggestion;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.MiuiSettings;
import com.android.settings.analytics.SearchStatItem;
import com.android.settings.cloud.AccessibilityDisableList;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settings.notify.SettingsNotifyEasyModeBuilder;
import com.android.settings.report.InternationalCompat;
import com.android.settings.search.SearchResult;
import com.android.settings.search.SearchResultItem;
import com.android.settings.search.SettingsGlobalSearcher;
import com.android.settings.search.appseparate.AppSearchResultItem;
import com.android.settings.search.appseparate.CollectResultProcessor;
import com.android.settings.search.appseparate.SeparateAppSearchHelper;
import com.android.settings.search.appseparate.SeparateAppSearchThread;
import com.android.settings.search.cloud.SearchCloudSortUtils;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settings.search.tree.SystemAppSettingsTree;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miui.os.Build;
import miui.settings.commonlib.MemoryOptimizationUtil;
import miuix.animation.Folme;
import miuix.animation.IFolme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.internal.util.AttributeResolver;
import miuix.recyclerview.widget.RecyclerView;
import miuix.view.SearchActionMode;

/* loaded from: classes.dex */
public class SettingsFragment extends BasePreferenceFragment implements View.OnTouchListener {
    private static final Double CLOUD_SORT_WEIGHT = Double.valueOf(0.5d);
    private View mAnchorView;
    private SearchStatItem mCurrSearchStatItem;
    private DeferredSetupHelper mDeferredSetupHelper;
    private SettingsGlobalSearcher mGlobalSearch;
    private MiuiSettings.HeaderAdapter mHeaderAdapter;
    private View mHintView;
    private volatile boolean mIsInActionMode;
    private RecyclerView mListView;
    private MiuiCustSplitUtils mMiuiCustSplitUtils;
    private MiuiSettings.ProxyHeaderViewAdapter mProxyAdapter;
    private SearchResultAdapter mSearchAdapter;
    private SearchHandler mSearchHandler;
    private EditText mSearchInput;
    private View mSearchLoadingView;
    private volatile SearchResult mSearchResult;
    private List<SearchResultItem> mSearchResultItems;
    private RecyclerView mSearchResultListView;
    private String mSearchText;
    private HandlerThread mSearchThread;
    private SeparateAppSearchThread mSeparateAppSearchThread;
    private TrimMemoryUtils mTrimMemoryUtils;
    private Handler mHandler = new DeferredSetupHandler();
    private List<String> mClickedList = new LinkedList();
    private HashSet<String> mSearchExcludeMap = null;
    boolean isFirstEnter = true;
    private SearchActionMode.Callback mSearchCallback = new SearchActionMode.Callback() { // from class: com.android.settings.SettingsFragment.6
        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            SettingsFragment.this.mCurrSearchStatItem = null;
            SearchActionMode searchActionMode = (SearchActionMode) actionMode;
            searchActionMode.setAnchorView(SettingsFragment.this.mAnchorView);
            searchActionMode.setAnimateView(SettingsFragment.this.mListView);
            searchActionMode.setResultView(SettingsFragment.this.mSearchResultListView);
            SettingsFragment.this.mSearchInput = searchActionMode.getSearchInput();
            SettingsFragment.this.mSearchInput.setOnEditorActionListener(SettingsFragment.this.mEditorActionListener);
            SettingsFragment.this.mSearchInput.addTextChangedListener(SettingsFragment.this.mTextWatcher);
            if (SettingsFeatures.isSplitTablet(SettingsFragment.this.getContext())) {
                SettingsFragment.this.mSearchInput.setImeOptions(268435456);
                SettingsFragment.this.mSearchInput.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsFragment.6.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (SettingsFragment.this.mMiuiCustSplitUtils.reachSplitSize()) {
                            SettingsFragment.this.mMiuiCustSplitUtils.finishAllSubActivities();
                        }
                    }
                });
            }
            SettingsFragment.this.mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.android.settings.SettingsFragment.6.2
                @Override // android.widget.TextView.OnEditorActionListener
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == 5) {
                        SettingsFragment.this.hideSoftKeyboard();
                        return true;
                    }
                    return false;
                }
            });
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
            SettingsFragment settingsFragment = SettingsFragment.this;
            if (settingsFragment.getNonEmptySearchResultCount(settingsFragment.mSearchResultItems) == 0 && !TextUtils.isEmpty(SettingsFragment.this.mSearchInput.getText().toString())) {
                HashMap hashMap = new HashMap();
                hashMap.put("SearchKeyWord", SettingsFragment.this.mSearchInput.getText().toString());
                InternationalCompat.trackReportEvent("setting_search_hotword", hashMap);
            }
            SettingsFragment.this.mIsInActionMode = false;
            SettingsFragment.this.mSearchInput.removeTextChangedListener(SettingsFragment.this.mTextWatcher);
            SettingsFragment.this.mSearchInput = null;
            SettingsFragment.this.mSearchResultListView.setVisibility(8);
            SettingsFragment.this.mSearchLoadingView.setVisibility(8);
            SettingsFragment.this.mListView.setVisibility(0);
            SettingsFragment.this.mSearchText = null;
            SettingsFragment.this.mSearchResultItems.clear();
            SettingsFragment.this.mSearchResultItems.add(SearchResultItem.EMPTY);
            SettingsFragment.this.mSearchAdapter.refresh(SettingsFragment.this.mSearchResultItems);
            if (SettingsFragment.this.mSearchHandler != null) {
                SettingsFragment.this.mSearchHandler.removeMessages(1);
            }
            if (SettingsFragment.this.mCurrSearchStatItem != null) {
                SettingsFragment.this.mCurrSearchStatItem.traceSearchEvent(false);
                SettingsFragment.this.mCurrSearchStatItem = null;
            }
            if (SettingsFeatures.isSplitTablet(SettingsFragment.this.getContext())) {
                ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.SettingsFragment.6.3
                    @Override // java.lang.Runnable
                    public void run() {
                        SettingsFragment.this.startSubIntentIfNeeded();
                    }
                });
            }
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            SettingsFragment.this.mIsInActionMode = true;
            return false;
        }
    };
    private Handler mMainHandler = new Handler();
    private boolean mIsSearchInited = false;
    private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() { // from class: com.android.settings.SettingsFragment.7
        @Override // android.widget.TextView.OnEditorActionListener
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            return true;
        }
    };
    private TextWatcher mTextWatcher = new TextWatcher() { // from class: com.android.settings.SettingsFragment.8
        private String mLastText = "";
        private boolean mInput = true;

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            SettingsFragment.this.updateSearch(editable.toString());
            SettingsFragment.this.mClickedList.clear();
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            this.mLastText = charSequence.toString();
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String charSequence2 = charSequence != null ? charSequence.toString() : "";
            if (!TextUtils.isEmpty(this.mLastText) && ((!charSequence2.contains(this.mLastText)) ^ this.mInput)) {
                if (!SettingsFragment.this.mSearchResultItems.isEmpty() && ((SearchResultItem) SettingsFragment.this.mSearchResultItems.get(0)).type == 1) {
                    MiStatInterfaceUtils.trackEvent("search_no_result");
                } else if (SettingsFragment.this.mClickedList.isEmpty()) {
                    MiStatInterfaceUtils.trackEvent("search_no_click");
                }
            }
            if (!SettingsFragment.this.mClickedList.isEmpty()) {
                MiStatInterfaceUtils.trackEvent("serach_click_record");
            }
            if (TextUtils.isEmpty(charSequence2)) {
                SettingsFragment.this.mSearchResultListView.setVisibility(8);
                SettingsFragment.this.mListView.setVisibility(0);
            } else {
                SettingsFragment.this.mSearchResultListView.setVisibility(0);
                SettingsFragment.this.mListView.setVisibility(8);
            }
            this.mInput = !charSequence2.contains(this.mLastText);
        }
    };

    /* loaded from: classes.dex */
    private class DeferredSetupHandler extends Handler {
        private DeferredSetupHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                SettingsFragment.this.updateHintView((Suggestion) message.obj);
            } else if (i == 1) {
                SettingsFragment.this.removeHintView();
            }
            super.handleMessage(message);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SearchHandler extends Handler {
        SearchHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            final List sortSearchItemByCloudData;
            int i = message.what;
            if (i != 1) {
                if (i != 2) {
                    return;
                }
                if (SettingsFragment.this.getActivity() != null && !SettingsFragment.this.mIsSearchInited) {
                    SettingsFragment.this.getActivity().getContentResolver().call(SettingsProvider.getSearchUri(""), SettingsProvider.METHOD_LOAD, "", (Bundle) null);
                    SettingsFragment.this.mIsSearchInited = true;
                }
                InternationalCompat.trackReportEvent("setting_search");
            } else if (!SettingsFragment.this.mIsInActionMode || SettingsFragment.this.mSearchResult == null) {
            } else {
                SettingsFragment.this.mMainHandler.post(new Runnable() { // from class: com.android.settings.SettingsFragment.SearchHandler.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (SettingsFragment.this.mIsInActionMode) {
                            SettingsFragment.this.mSearchLoadingView.setVisibility(0);
                        }
                    }
                });
                final String str = (String) message.obj;
                List<SearchResultItem> mergeSearchResults = SettingsFragment.this.getMergeSearchResults(SettingsFragment.this.mSearchResult.getSearchResultList(SettingsFragment.this.getContext(), str), SeparateAppSearchHelper.getInstance(SettingsFragment.this.getContext()).getSearchResult(str));
                if (SettingsFragment.this.mGlobalSearch != null && !SettingsFeatures.isSplitTablet(SettingsFragment.this.getContext())) {
                    SettingsFragment settingsFragment = SettingsFragment.this;
                    mergeSearchResults = settingsFragment.mergeGlobalResults(mergeSearchResults, settingsFragment.mGlobalSearch.search(str));
                }
                if (SettingsFragment.this.mSearchExcludeMap != null) {
                    sortSearchItemByCloudData = SettingsFragment.this.sortSearchItemByCloudData(SearchResult.removeExcludeItem(SettingsFragment.this.mSearchExcludeMap, mergeSearchResults));
                } else {
                    sortSearchItemByCloudData = SettingsFragment.this.sortSearchItemByCloudData(mergeSearchResults);
                }
                SettingsFragment.this.mMainHandler.post(new Runnable() { // from class: com.android.settings.SettingsFragment.SearchHandler.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!SettingsFragment.this.mIsInActionMode || SettingsFragment.this.mSearchResult == null) {
                            return;
                        }
                        SettingsFragment.this.mSearchResultItems = sortSearchItemByCloudData;
                        if (SettingsFragment.this.mCurrSearchStatItem == null) {
                            SettingsFragment.this.mCurrSearchStatItem = new SearchStatItem();
                        }
                        SettingsFragment.this.mCurrSearchStatItem.clear();
                        SettingsFragment.this.mCurrSearchStatItem.setKeyWork(str);
                        SearchStatItem searchStatItem = SettingsFragment.this.mCurrSearchStatItem;
                        SettingsFragment settingsFragment2 = SettingsFragment.this;
                        searchStatItem.setSearchResultCount(settingsFragment2.getNonEmptySearchResultCount(settingsFragment2.mSearchResultItems));
                        SettingsFragment.this.mSearchAdapter.refresh(SettingsFragment.this.mSearchResultItems);
                        SettingsFragment.this.mSearchLoadingView.setVisibility(8);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SearchItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView path;
        public TextView summary;

        public SearchItemViewHolder(View view) {
            super(view);
            int intValue = ((Integer) view.getTag()).intValue();
            if (intValue == 0) {
                this.icon = (ImageView) view.findViewById(R.id.settings_search_item_image);
                this.name = (TextView) view.findViewById(R.id.settings_search_item_name);
                this.path = (TextView) view.findViewById(R.id.settings_search_item_path);
            } else if (intValue != 2) {
            } else {
                this.icon = (ImageView) view.findViewById(R.id.settings_search_item_image);
                this.name = (TextView) view.findViewById(R.id.settings_search_item_name);
                this.summary = (TextView) view.findViewById(R.id.settings_search_item_path);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SearchResultAdapter extends RecyclerView.Adapter<SearchItemViewHolder> {
        private Context mContext;
        private LayoutInflater mInflater;
        List<SearchResultItem> mList;
        private PackageManager mPackageManager;
        private final int ITEM_TYPE_COUNT = 3;
        private String mLanguage = SettingsFragment.access$3100();

        public SearchResultAdapter(Context context, List<SearchResultItem> list) {
            this.mContext = context;
            this.mList = list;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.mPackageManager = this.mContext.getPackageManager();
        }

        private int getSearchResultItemType(SearchResultItem searchResultItem) {
            if (searchResultItem != null) {
                return searchResultItem.type;
            }
            return -1;
        }

        private void setSearchResultView(View view, SearchItemViewHolder searchItemViewHolder, final SearchResultItem searchResultItem, final int i) {
            Drawable drawable;
            ResolveInfo resolveActivity;
            ActivityInfo activityInfo;
            String str = searchResultItem.title;
            if (TextUtils.isEmpty(str)) {
                str = searchResultItem.path.split("/")[r0.length - 1];
            }
            SettingsFragment settingsFragment = SettingsFragment.this;
            SpannableStringBuilder highlight = settingsFragment.highlight(str, settingsFragment.mSearchText, this.mLanguage);
            if (!TextUtils.isEmpty(str)) {
                searchItemViewHolder.name.setText(highlight);
            }
            if (!TextUtils.isEmpty(searchResultItem.path)) {
                searchItemViewHolder.path.setText(searchResultItem.path);
            }
            Drawable drawable2 = null;
            int i2 = 0;
            if (!TextUtils.isEmpty(searchResultItem.icon)) {
                if (TextUtils.equals(searchResultItem.icon, SystemAppSettingsTree.SYSTEM_APP_MARK) && (resolveActivity = SettingsFragment.this.getPackageManager().resolveActivity(searchResultItem.intent, 0)) != null && (activityInfo = resolveActivity.activityInfo) != null) {
                    drawable2 = activityInfo.applicationInfo.loadIcon(SettingsFragment.this.getPackageManager());
                }
                if (drawable2 == null) {
                    try {
                        Resources resourcesForApplication = SettingsFragment.this.getPackageManager().getResourcesForApplication(searchResultItem.pkg);
                        drawable2 = resourcesForApplication.getDrawable(resourcesForApplication.getIdentifier(searchResultItem.icon, "drawable", searchResultItem.pkg));
                    } catch (PackageManager.NameNotFoundException | Resources.NotFoundException unused) {
                    }
                }
                if (drawable2 == null) {
                    i2 = SettingsFragment.this.getResources().getIdentifier(searchResultItem.icon, "drawable", "com.android.settings");
                }
            }
            if (searchResultItem.isGlobalSearch && (drawable = searchResultItem.globalSearchIcon) != null) {
                searchItemViewHolder.icon.setImageDrawable(drawable);
                searchItemViewHolder.icon.setImageDrawable(searchResultItem.globalSearchIcon);
            } else if (i2 != 0) {
                searchItemViewHolder.icon.setImageResource(i2);
            } else {
                searchItemViewHolder.icon.setImageDrawable(drawable2);
            }
            view.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsFragment.SearchResultAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    SettingsFragment.this.hideSoftKeyboard();
                    SettingsFragment.this.setSearchResultItemViewJump(view2, searchResultItem);
                    SettingsFragment.this.searchItemClickStat(i, searchResultItem.resource);
                }
            });
        }

        private void setSeparateAppSearchResultView(View view, SearchItemViewHolder searchItemViewHolder, final SearchResultItem searchResultItem, final int i) {
            String str = searchResultItem.title;
            SettingsFragment settingsFragment = SettingsFragment.this;
            SpannableStringBuilder highlight = settingsFragment.highlight(str, settingsFragment.mSearchText, this.mLanguage);
            AppSearchResultItem appSearchResultItem = (AppSearchResultItem) searchResultItem;
            if (!TextUtils.isEmpty(str)) {
                searchItemViewHolder.name.setText(highlight);
            }
            if (TextUtils.isEmpty(searchResultItem.summary)) {
                String appName = appSearchResultItem.getAppName();
                TextView textView = searchItemViewHolder.summary;
                if (TextUtils.isEmpty(appName)) {
                    appName = "";
                }
                textView.setText(appName);
            } else {
                searchItemViewHolder.summary.setText(searchResultItem.summary);
            }
            PackageManager packageManager = SettingsFragment.this.getPackageManager();
            UserHandle appUserHandle = appSearchResultItem.getAppUserHandle();
            String str2 = appSearchResultItem.getInfo().packageName;
            Drawable iconDrawableById = MiuiUtils.getIconDrawableById(this.mContext, appSearchResultItem.getIconResId(), str2);
            if (iconDrawableById == null) {
                iconDrawableById = (MiuiUtils.hasLauncherIcon(this.mContext, str2) || !CollectResultProcessor.sInvalidBadgedIconPackageSet.contains(str2)) ? Utils.getBadgedIcon(IconDrawableFactory.newInstance(view.getContext()), packageManager, str2, appUserHandle.getIdentifier()) : this.mContext.getDrawable(R.drawable.ic_other_advanced_settings);
            }
            searchItemViewHolder.icon.setImageDrawable(iconDrawableById);
            view.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsFragment.SearchResultAdapter.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    SettingsFragment.this.setSearchResultItemViewJump(view2, searchResultItem);
                    SettingsFragment.this.searchItemClickStat(i, searchResultItem.resource);
                }
            });
        }

        public SearchResultItem getItem(int i) {
            if (i >= this.mList.size()) {
                return this.mList.get(r1.size() - 1);
            }
            return this.mList.get(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            List<SearchResultItem> list = this.mList;
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return getSearchResultItemType(getItem(i));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(SearchItemViewHolder searchItemViewHolder, int i) {
            SearchResultItem item = getItem(i);
            int i2 = item.type;
            View view = searchItemViewHolder.itemView;
            if (i2 == 0) {
                view.setBackground(AttributeResolver.resolveDrawable(SettingsFragment.this.getContext(), R.attr.preferenceItemBackground));
            } else if (i2 == 2) {
                view.setBackground(AttributeResolver.resolveDrawable(SettingsFragment.this.getContext(), R.attr.preferenceItemBackground));
            }
            if (i2 == 0) {
                setSearchResultView(view, searchItemViewHolder, item, i);
            } else if (i2 != 1) {
                if (i2 != 2) {
                    return;
                }
                setSeparateAppSearchResultView(view, searchItemViewHolder, item, i);
            } else {
                View findViewById = view.findViewById(R.id.empty_img);
                if (findViewById == null || !MiuiUtils.isLower4GB()) {
                    return;
                }
                findViewById.setVisibility(8);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public SearchItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View inflate = i != 0 ? i != 1 ? i != 2 ? null : this.mInflater.inflate(R.layout.search_settings_element, viewGroup, false) : this.mInflater.inflate(R.layout.search_result_empty, viewGroup, false) : this.mInflater.inflate(R.layout.search_settings_element, viewGroup, false);
            if (inflate != null) {
                inflate.setTag(Integer.valueOf(i));
                if (SettingsFragment.this.getActivity() != null && i != 1) {
                    Folme.useAt(inflate).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).setBackgroundColor(SettingsFragment.this.getResources().getColor(R.color.miuisettings_item_touch_color, SettingsFragment.this.getActivity().getTheme())).handleTouchOf(inflate, new AnimConfig[0]);
                }
            }
            return new SearchItemViewHolder(inflate);
        }

        public void refresh(List<SearchResultItem> list) {
            this.mList = list;
            notifyDataSetChanged();
        }
    }

    static /* synthetic */ String access$3100() {
        return getLanguage();
    }

    private void adapterAccessibility(View view) {
        if (SettingsFeatures.isSplitTablet(getContext())) {
            return;
        }
        this.isFirstEnter = true;
        if (view == null) {
            return;
        }
        ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegateCompat() { // from class: com.android.settings.SettingsFragment.4
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public boolean performAccessibilityAction(View view2, int i, Bundle bundle) {
                SettingsFragment settingsFragment = SettingsFragment.this;
                if (settingsFragment.isFirstEnter && i == 64) {
                    settingsFragment.isFirstEnter = false;
                    View findViewById = settingsFragment.getActivity().getWindow().getDecorView().findViewById(R.id.action_bar_title_expand);
                    if (findViewById != null) {
                        findViewById.sendAccessibilityEvent(8);
                        return true;
                    }
                    return true;
                }
                return super.performAccessibilityAction(view2, i, bundle);
            }
        });
    }

    private void addHintView() {
        if (this.mHintView != null) {
            return;
        }
        View findViewWithTag = this.mListView.findViewWithTag("deferred_setup_hint");
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (findViewWithTag == null) {
            findViewWithTag = activity.getLayoutInflater().inflate(R.layout.deferred_setup_hint, (ViewGroup) null);
            findViewWithTag.setTag("deferred_setup_hint");
            MiuiSettings.ProxyHeaderViewAdapter proxyHeaderViewAdapter = this.mProxyAdapter;
            if (proxyHeaderViewAdapter != null) {
                proxyHeaderViewAdapter.addDeferedSetupView(findViewWithTag);
            }
        }
        this.mHintView = findViewWithTag;
        ((TextView) findViewWithTag.findViewById(R.id.deferred_setup_title)).setText(R.string.deferred_setup_hintViewTitle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureSearchHandler() {
        if (this.mSearchThread == null) {
            HandlerThread handlerThread = new HandlerThread("SettingsFragment-Search");
            this.mSearchThread = handlerThread;
            handlerThread.start();
            this.mSearchHandler = new SearchHandler(this.mSearchThread.getLooper());
        }
        if (this.mSeparateAppSearchThread == null) {
            this.mSeparateAppSearchThread = new SeparateAppSearchThread("SettingsFragment-SeparateAppSearch", this);
        }
    }

    private static String getLanguage() {
        try {
            return ActivityManagerNative.getDefault().getConfiguration().locale.toString();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    private MiuiCustSplitUtils getMiuiCustSplitUtils() {
        if (this.mMiuiCustSplitUtils == null) {
            this.mMiuiCustSplitUtils = new MiuiCustSplitUtilsImpl(getActivity());
        }
        return this.mMiuiCustSplitUtils;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getNonEmptySearchResultCount(List<SearchResultItem> list) {
        if (list == null || list.isEmpty() || list.get(0).type == 1) {
            return 0;
        }
        return list.size();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideSoftKeyboard() {
        if (getActivity() == null) {
            return;
        }
        ((InputMethodManager) getActivity().getSystemService("input_method")).hideSoftInputFromWindow(this.mSearchResultListView.getWindowToken(), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SpannableStringBuilder highlight(String str, String str2, String str3) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        Matcher matcher = Pattern.compile(str2, 18).matcher(str);
        while (matcher.find()) {
            int end = matcher.end();
            if (end > spannableStringBuilder.length()) {
                end = spannableStringBuilder.length();
            }
            spannableStringBuilder.setSpan(new ForegroundColorSpan(-65536), matcher.start(), end, 33);
        }
        return spannableStringBuilder;
    }

    private void initGlobalSearchIfNeed() {
        if (this.mGlobalSearch != null || getActivity() == null) {
            return;
        }
        SettingsGlobalSearcher settingsGlobalSearcher = new SettingsGlobalSearcher(getActivity());
        this.mGlobalSearch = settingsGlobalSearcher;
        settingsGlobalSearcher.requestGlobalSearchUpdate();
    }

    private boolean isStartUpdaterResource(SearchResultItem searchResultItem) {
        return searchResultItem.resource.equals("miui_updater") || searchResultItem.resource.equals("device_miui_version");
    }

    private void loadRemovableHint() {
        View findViewWithTag = this.mListView.findViewWithTag("removable_hint");
        FragmentActivity activity = getActivity();
        final SettingsNotifyEasyModeBuilder.SettingsNotify build = SettingsNotifyEasyModeBuilder.getInstance().build(activity.getApplicationContext());
        if (build == null) {
            if (findViewWithTag != null) {
                this.mProxyAdapter.removeRemovableHintView(findViewWithTag);
                return;
            }
            return;
        }
        if (findViewWithTag == null) {
            findViewWithTag = activity.getLayoutInflater().inflate(R.layout.deferred_setup_hint, (ViewGroup) null);
            findViewWithTag.setTag("removable_hint");
            this.mProxyAdapter.addRemovableHintView(findViewWithTag);
        }
        ((TextView) findViewWithTag.findViewById(R.id.deferred_setup_title)).setText(R.string.easymode_hint);
        ((ViewGroup) findViewWithTag.findViewById(R.id.hint_layout)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FragmentActivity activity2 = SettingsFragment.this.getActivity();
                if (activity2 != null) {
                    build.goToTarget(activity2);
                }
            }
        });
    }

    private Double normalizeScore(Double d) {
        return d.doubleValue() < 1.0d ? Double.valueOf(0.0d) : Double.valueOf((d.doubleValue() - 1.0d) / 3.0d);
    }

    private void releaseSettingsTree() {
        if (getActivity() == null || !this.mIsSearchInited) {
            return;
        }
        getActivity().getContentResolver().call(SettingsProvider.getSearchUri(""), "release", "", (Bundle) null);
        this.mIsSearchInited = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeHintView() {
        View findViewWithTag;
        miuix.recyclerview.widget.RecyclerView recyclerView = this.mListView;
        if (recyclerView == null || (findViewWithTag = recyclerView.findViewWithTag("deferred_setup_hint")) == null) {
            return;
        }
        this.mProxyAdapter.removeDeferedSetupView(findViewWithTag);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void searchItemClickStat(int i, String str) {
        SearchStatItem searchStatItem = this.mCurrSearchStatItem;
        if (searchStatItem != null) {
            searchStatItem.setClickedItemOrder(i);
            this.mCurrSearchStatItem.setClickedResource(str);
            this.mCurrSearchStatItem.traceSearchEvent(true);
            this.mCurrSearchStatItem.setIsAlreadyStat(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSearchResultItemViewJump(View view, SearchResultItem searchResultItem) {
        SearchStatItem searchStatItem = this.mCurrSearchStatItem;
        if (searchStatItem == null) {
            return;
        }
        if (!TextUtils.isEmpty(searchStatItem.getKeyWork())) {
            HashMap hashMap = new HashMap();
            hashMap.put("SearchKeyWord", this.mCurrSearchStatItem.getKeyWork());
            InternationalCompat.trackReportEvent("setting_search_hotword", hashMap);
        }
        InternationalCompat.trackReportEvent("setting_search_done");
        if (searchResultItem.resource != null && isStartUpdaterResource(searchResultItem) && searchResultItem.status == 3) {
            MiuiAboutPhoneUtils.startUpdater(getActivity());
        } else {
            String str = searchResultItem.resource;
            if (str != null && str.equals("virtual_keyboards_for_work_title")) {
                UserHandle managedProfile = Utils.getManagedProfile(UserManager.get(getContext()));
                if (SettingsFeatures.isFoldDevice()) {
                    searchResultItem.intent.addMiuiFlags(4);
                }
                getContext().startActivityAsUser(searchResultItem.intent, managedProfile);
            } else if (searchResultItem.intent != null && MiuiUtils.getInstance().canFindActivity(getContext(), searchResultItem.intent)) {
                if (SettingsFeatures.isFoldDevice()) {
                    searchResultItem.intent.addMiuiFlags(4);
                }
                getContext().startActivity(searchResultItem.intent);
            }
        }
        MiuiSettings miuiSettings = (MiuiSettings) getActivity();
        if (miuiSettings != null) {
            miuiSettings.disableSelectedPosition();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<SearchResultItem> sortSearchItemByCloudData(List<SearchResultItem> list) {
        Double cloudWeight = SearchCloudSortUtils.getInstance(getContext()).getCloudWeight();
        for (int i = 0; i < list.size(); i++) {
            SearchResultItem searchResultItem = list.get(i);
            Double d = SearchCloudSortUtils.getInstance(getContext()).get(searchResultItem.resource);
            if (d == null) {
                d = Double.valueOf(1.0d);
            }
            if (cloudWeight == null) {
                cloudWeight = CLOUD_SORT_WEIGHT;
            }
            try {
                searchResultItem.score = (cloudWeight.doubleValue() * normalizeScore(d).doubleValue()) + (searchResultItem.score * (1.0d - cloudWeight.doubleValue()));
            } catch (Exception e) {
                Log.e("SettingsFragment", e.getMessage());
                e.printStackTrace();
            }
            list.set(i, searchResultItem);
        }
        Collections.sort(list);
        return list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSubIntentIfNeeded() {
        if (!this.mMiuiCustSplitUtils.reachSplitSize() || getActivity() == null || this.mMiuiCustSplitUtils.getCurrentSubIntent() == null || !getActivity().hasWindowFocus()) {
            return;
        }
        getActivity().startActivity(this.mMiuiCustSplitUtils.getCurrentSubIntent());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHintView(final Suggestion suggestion) {
        if (suggestion == null) {
            return;
        }
        if (this.mHintView == null) {
            addHintView();
        }
        ((TextView) this.mHintView.findViewById(R.id.deferred_setup_title)).setText(suggestion.getTitle());
        ViewGroup viewGroup = (ViewGroup) this.mHintView.findViewById(R.id.hint_layout);
        viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    suggestion.getPendingIntent().send();
                } catch (PendingIntent.CanceledException unused) {
                    Log.w("SettingsFragment", "Failed to start suggestion " + ((Object) suggestion.getTitle()));
                }
            }
        });
        final IFolme useAt = Folme.useAt(viewGroup);
        useAt.touch().setScale(1.0f, ITouchStyle.TouchType.DOWN);
        viewGroup.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.SettingsFragment.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                useAt.touch().onMotionEvent(motionEvent);
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSearch(String str) {
        if (Objects.equals(this.mSearchText, str)) {
            return;
        }
        ensureSearchHandler();
        this.mSearchHandler.removeMessages(1);
        this.mSearchHandler.obtainMessage(1, str).sendToTarget();
        if (TextUtils.isEmpty(this.mSearchText)) {
            this.mSearchLoadingView.setVisibility(0);
        }
        this.mSearchText = str;
    }

    @Override // com.android.settings.BasePreferenceFragment
    public void buildAdapter() {
        super.buildAdapter();
        MiuiSettings miuiSettings = (MiuiSettings) getActivity();
        Objects.requireNonNull(miuiSettings);
        MiuiSettings.HeaderAdapter headerAdapter = new MiuiSettings.HeaderAdapter(miuiSettings, this.mHeaders, miuiSettings.getAuthenticatorHelper(), false);
        this.mHeaderAdapter = headerAdapter;
        headerAdapter.setHasStableIds(true);
        this.mProxyAdapter = new MiuiSettings.ProxyHeaderViewAdapter(this.mHeaderAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(1);
        this.mListView.setLayoutManager(linearLayoutManager);
        this.mListView.setAdapter(this.mProxyAdapter);
        this.mSearchResultListView.setVisibility(8);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setOrientation(1);
        this.mSearchResultListView.setLayoutManager(linearLayoutManager2);
        this.mSearchResultListView.setAdapter(this.mSearchAdapter);
        startSelectHeader();
    }

    @Override // com.android.settings.BasePreferenceFragment
    protected int getHeadersResourceId() {
        return R.xml.settings_headers;
    }

    public List<SearchResultItem> getMergeSearchResults(List<SearchResultItem> list, List<SearchResultItem> list2) {
        long currentTimeMillis = System.currentTimeMillis();
        int i = 0;
        int size = list != null ? list.size() : 0;
        int size2 = list2 != null ? list2.size() : 0;
        LinkedList linkedList = new LinkedList();
        int nonEmptySearchResultCount = getNonEmptySearchResultCount(list);
        if (size2 <= 0 || nonEmptySearchResultCount > 0) {
            if (size2 <= 0) {
                return list;
            }
            int i2 = 0;
            while (i < size && i2 < size2) {
                if (list.get(i).score >= list2.get(i2).score) {
                    linkedList.add(list.get(i));
                    i++;
                } else {
                    linkedList.add(list2.get(i2));
                    i2++;
                }
            }
            while (i < size) {
                linkedList.add(list.get(i));
                i++;
            }
            while (i2 < size2) {
                linkedList.add(list2.get(i2));
                i2++;
            }
            SearchUtils.logCost(currentTimeMillis, System.currentTimeMillis(), "-");
            return linkedList;
        }
        return list2;
    }

    public List<SearchResultItem> mergeGlobalResults(List<SearchResultItem> list, List<SearchResultItem> list2) {
        if (getNonEmptySearchResultCount(list) <= 0) {
            return list2;
        }
        if (getNonEmptySearchResultCount(list2) <= 0) {
            return list;
        }
        list.addAll(0, list2);
        SettingsGlobalSearcher settingsGlobalSearcher = this.mGlobalSearch;
        if (settingsGlobalSearcher != null) {
            settingsGlobalSearcher.removeDuplicateSearchResult(list);
        }
        return list;
    }

    @Override // com.android.settings.BasePreferenceFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LinkedList linkedList = new LinkedList();
        this.mSearchResultItems = linkedList;
        linkedList.add(SearchResultItem.EMPTY);
        this.mSearchAdapter = new SearchResultAdapter(getActivity(), this.mSearchResultItems);
        AccessibilityDisableList.updateDisableSet(getContext().getApplicationContext());
        initGlobalSearchIfNeed();
        this.mSearchAdapter.setHasStableIds(true);
        this.mSearchResult = new SearchResult();
        InternationalCompat.reportSwitchStatus(getActivity());
        InternationalCompat.trackReportEvent("settiing_homepage_show");
        if (SettingsFeatures.isSplitTablet(getContext())) {
            this.mMiuiCustSplitUtils = getMiuiCustSplitUtils();
        }
        if (this.mTrimMemoryUtils == null) {
            TrimMemoryUtils trimMemoryUtils = new TrimMemoryUtils();
            this.mTrimMemoryUtils = trimMemoryUtils;
            trimMemoryUtils.addIdleHandler();
        }
        this.mSearchExcludeMap = SearchResult.getSearchExcludeMap();
    }

    @Override // com.android.settings.BasePreferenceFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mTextWatcher.onTextChanged(null, 0, 0, 0);
        HandlerThread handlerThread = this.mSearchThread;
        if (handlerThread != null) {
            handlerThread.quit();
            this.mSearchThread = null;
        }
        SeparateAppSearchThread separateAppSearchThread = this.mSeparateAppSearchThread;
        if (separateAppSearchThread != null) {
            separateAppSearchThread.quit();
            this.mSeparateAppSearchThread = null;
        }
        this.mSearchResult = null;
        SettingsGlobalSearcher settingsGlobalSearcher = this.mGlobalSearch;
        if (settingsGlobalSearcher != null) {
            settingsGlobalSearcher.unregisterSyncGlobalSearchCompleted();
        }
        TrimMemoryUtils trimMemoryUtils = this.mTrimMemoryUtils;
        if (trimMemoryUtils != null) {
            trimMemoryUtils.removeIdleHandler();
            this.mTrimMemoryUtils = null;
        }
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.settings_search_fragment, viewGroup, false);
        miuix.recyclerview.widget.RecyclerView recyclerView = (miuix.recyclerview.widget.RecyclerView) inflate.findViewById(R.id.scroll_headers);
        this.mListView = recyclerView;
        recyclerView.setFocusable(true);
        this.mListView.setFocusableInTouchMode(true);
        this.mListView.setItemAnimator(null);
        this.mListView.setItemViewCacheSize(-1);
        miuix.recyclerview.widget.RecyclerView recyclerView2 = (miuix.recyclerview.widget.RecyclerView) inflate.findViewById(R.id.search_result);
        this.mSearchResultListView = recyclerView2;
        recyclerView2.setFocusable(true);
        this.mSearchResultListView.setFocusableInTouchMode(true);
        this.mSearchResultListView.setOnTouchListener(this);
        this.mSearchResultListView.setItemAnimator(null);
        this.mSearchLoadingView = inflate.findViewById(R.id.search_loading);
        try {
            TypedArray obtainStyledAttributes = getActivity().obtainStyledAttributes((AttributeSet) null, new int[]{16842836});
            this.mSearchLoadingView.setBackground(obtainStyledAttributes.getDrawable(0));
            obtainStyledAttributes.recycle();
        } catch (Exception e) {
            this.mSearchLoadingView.setBackgroundColor(-1);
            Log.w("SettingsFragment", "Fail to find windowBackground in current context", e);
        }
        return inflate;
    }

    @Override // com.android.settings.BasePreferenceFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        loadRemovableHint();
        initGlobalSearchIfNeed();
        if (this.mSeparateAppSearchThread == null || !this.mIsInActionMode) {
            return;
        }
        this.mSeparateAppSearchThread.sendInitMessage();
    }

    @Override // com.android.settings.BasePreferenceFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (Build.IS_INTERNATIONAL_BUILD) {
            if (getContext() != null && getContext().getSharedPreferences("DEFERRED_SETUP", 0).getBoolean("isShow", false)) {
                addHintView();
            }
            if (this.mDeferredSetupHelper == null) {
                this.mDeferredSetupHelper = new DeferredSetupHelper(getContext(), this.mHandler);
            }
            this.mDeferredSetupHelper.startLoad();
        }
    }

    @Override // com.android.settings.BasePreferenceFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        DeferredSetupHelper deferredSetupHelper;
        super.onStop();
        if (Build.IS_INTERNATIONAL_BUILD && (deferredSetupHelper = this.mDeferredSetupHelper) != null) {
            deferredSetupHelper.stop();
            this.mDeferredSetupHelper = null;
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view == this.mSearchResultListView) {
            hideSoftKeyboard();
            return false;
        }
        return false;
    }

    @Override // com.android.settings.BasePreferenceFragment
    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        if (80 == i) {
            Log.i("SettingsFragment", "onTrimMemory TRIM_MEMORY_COMPLETE");
            SettingsGlobalSearcher settingsGlobalSearcher = this.mGlobalSearch;
            if (settingsGlobalSearcher != null) {
                settingsGlobalSearcher.unregisterSyncGlobalSearchCompleted();
                this.mGlobalSearch = null;
            }
            releaseSettingsTree();
            SeparateAppSearchThread separateAppSearchThread = this.mSeparateAppSearchThread;
            if (separateAppSearchThread != null) {
                separateAppSearchThread.sendReleaseMessage();
            }
            SearchUtils.clearPackageExistedCache();
            MemoryOptimizationUtil.sendMemoryOptimizationMsg(getContext(), 0L);
        }
    }

    @Override // com.android.settings.BasePreferenceFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        View findViewById = view.findViewById(R.id.header_view);
        this.mAnchorView = findViewById;
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (SettingsFeatures.isSplitTablet(SettingsFragment.this.getContext()) && SettingsFragment.this.mMiuiCustSplitUtils.reachSplitSize()) {
                    SettingsFragment.this.mMiuiCustSplitUtils.finishAllSubActivities();
                }
                SettingsFragment.this.ensureSearchHandler();
                if (SettingsFragment.this.mListView != null) {
                    SettingsFragment.this.mListView.setVisibility(8);
                }
                if (SettingsFragment.this.mSearchInput != null) {
                    SettingsFragment.this.mSearchInput.addTextChangedListener(SettingsFragment.this.mTextWatcher);
                }
                SettingsFragment.this.mSearchHandler.obtainMessage(2).sendToTarget();
                SettingsFragment.this.mSeparateAppSearchThread.sendInitMessage();
                SettingsFragment settingsFragment = SettingsFragment.this;
                settingsFragment.startActionMode(settingsFragment.mSearchCallback);
            }
        });
        ((TextView) this.mAnchorView.findViewById(16908297)).setHint(R.string.search_input_hint);
        adapterAccessibility(this.mAnchorView);
    }
}
