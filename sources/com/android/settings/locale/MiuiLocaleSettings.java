package com.android.settings.locale;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;
import com.android.settings.BaseFragment;
import com.android.settings.MiuiSettings;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.report.InternationalCompat;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miui.os.Build;
import miui.os.IMiuiInitObserver;
import miui.os.MiuiInit;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.ProgressDialog;

/* loaded from: classes.dex */
public class MiuiLocaleSettings extends BaseFragment implements AdapterView.OnItemClickListener {
    private ActionBar mActionBar;
    private Context mContext;
    private ProgressDialog mDialog;
    private InputMethodManager mInputMethodManager;
    private LocaleAdapter mLocaleAdapter;
    private ListView mLocaleList;
    private LocaleSettingsHelper mLocaleSettingsHelper;
    private AbsListView.OnScrollListener mOnListScrollListener;
    private QueryAsyncTask mQueryAsyncTask;
    private String mSearchText;
    private EditText mSearchView;
    private LinearLayout mSearchViewEmpty;
    private String mSelectedLocale;
    private boolean mOnSetup = false;
    private Handler mHandler = new Handler() { // from class: com.android.settings.locale.MiuiLocaleSettings.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (MiuiLocaleSettings.this.getActivity() == null) {
                Log.d("MiuiLocaleSettings", "[mHandler.handleMessage]getActivity is null");
            } else if (message.what != 1) {
            } else {
                MiuiLocaleSettings.this.finish(false);
            }
        }
    };

    /* loaded from: classes.dex */
    private static class InstallAppTask implements Runnable {
        private WeakReference<MiuiLocaleSettings> mHostFragmentRef;

        public InstallAppTask(MiuiLocaleSettings miuiLocaleSettings) {
            this.mHostFragmentRef = new WeakReference<>(miuiLocaleSettings);
        }

        @Override // java.lang.Runnable
        public void run() {
            MiuiLocaleSettings miuiLocaleSettings;
            if (!MiuiInit.installPreinstallApp() || (miuiLocaleSettings = this.mHostFragmentRef.get()) == null) {
                return;
            }
            miuiLocaleSettings.setOnSetup(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class LocaleAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<LocaleInfo> mLocaleInfos;

        /* loaded from: classes.dex */
        final class ViewHolder {
            RadioButton radioButton;
            TextView title;

            ViewHolder() {
            }
        }

        public LocaleAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
            this.mLocaleInfos = MiuiLocaleSettings.this.mLocaleSettingsHelper.queryLocaleInfoItems(null, "", false);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mLocaleInfos.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mLocaleInfos.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r1v1, types: [android.text.SpannableStringBuilder] */
        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            LocaleInfo localeInfo;
            if (view == null) {
                view = this.mInflater.inflate(R.layout.locale_search_item, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.radioButton = (RadioButton) view.findViewById(R.id.locale);
                viewHolder.title = (TextView) view.findViewById(R.id.title);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (viewHolder != null && viewHolder.radioButton != null && (localeInfo = (LocaleInfo) getItem(i)) != null && localeInfo.getCountryCode() != null) {
                String displayName = localeInfo.getDisplayName();
                MiuiLocaleSettings miuiLocaleSettings = MiuiLocaleSettings.this;
                ?? highlight = miuiLocaleSettings.highlight(displayName, miuiLocaleSettings.mSearchText);
                TextView textView = viewHolder.title;
                if (!TextUtils.isEmpty(MiuiLocaleSettings.this.mSearchText)) {
                    displayName = highlight;
                }
                textView.setText(displayName);
                viewHolder.radioButton.setChecked(localeInfo.getCountryCode().equalsIgnoreCase(MiuiLocaleSettings.this.mSelectedLocale));
            }
            return view;
        }

        public void setData(List<LocaleInfo> list) {
            this.mLocaleInfos = list;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class QueryAsyncTask extends AsyncTask<Void, Void, List<LocaleInfo>> {
        private final WeakReference<MiuiLocaleSettings> mHostFragmentRef;
        private final String mQuery;

        QueryAsyncTask(MiuiLocaleSettings miuiLocaleSettings, String str) {
            this.mHostFragmentRef = new WeakReference<>(miuiLocaleSettings);
            this.mQuery = str;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public List<LocaleInfo> doInBackground(Void... voidArr) {
            MiuiLocaleSettings miuiLocaleSettings = this.mHostFragmentRef.get();
            return (miuiLocaleSettings == null || miuiLocaleSettings.mQueryAsyncTask == null || miuiLocaleSettings.mQueryAsyncTask.isCancelled()) ? Collections.emptyList() : miuiLocaleSettings.mLocaleSettingsHelper.queryLocaleInfoItems(miuiLocaleSettings.mQueryAsyncTask, this.mQuery, true);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(List<LocaleInfo> list) {
            MiuiLocaleSettings miuiLocaleSettings = this.mHostFragmentRef.get();
            if (miuiLocaleSettings == null) {
                return;
            }
            miuiLocaleSettings.mLocaleAdapter.setData(list);
            miuiLocaleSettings.mLocaleAdapter.notifyDataSetChanged();
            miuiLocaleSettings.mLocaleList.setEmptyView(miuiLocaleSettings.mSearchViewEmpty);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SpannableStringBuilder highlight(String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        Matcher matcher = Pattern.compile(str2.toUpperCase(), 16).matcher(str.toUpperCase());
        while (matcher.find()) {
            spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.search_locale_highlight_text)), matcher.start(), matcher.end(), 33);
        }
        return spannableStringBuilder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOnSetup(boolean z) {
        this.mOnSetup = z;
    }

    private void showInstallPreInstallAppDialog() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        this.mDialog = progressDialog;
        progressDialog.setMessage(getString(R.string.install_preinstall_app));
        this.mDialog.setCancelable(false);
        this.mDialog.setIndeterminate(true);
        this.mDialog.show();
        this.mHandler.sendEmptyMessageDelayed(1, 2000L);
    }

    @Override // com.android.settings.BaseFragment
    public void finish() {
        finish(true);
    }

    public void finish(boolean z) {
        EditText editText;
        if (z && (editText = this.mSearchView) != null) {
            this.mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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
            fragmentManager.popBackStackImmediate();
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(false);
        this.mContext = getActivity();
        boolean z = RegionUtils.IS_MEXICO_TELCEL;
        this.mSelectedLocale = z ? "mx" : Build.getRegion();
        if (bundle != null) {
            boolean z2 = bundle.getBoolean("on_setup");
            this.mOnSetup = z2;
            if (z2) {
                showInstallPreInstallAppDialog();
            }
        }
        String[] custVariants = z ? new String[]{"mx"} : MiuiInit.getCustVariants();
        if (custVariants == null || custVariants.length == 0) {
            Log.w("MiuiLocaleSettings", "can not get cust variants, finish");
            finish(false);
        }
        InternationalCompat.trackReportEvent("setting_Additional_settings_region");
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.locale_search, viewGroup, false);
        this.mSearchViewEmpty = (LinearLayout) inflate.findViewById(R.id.search_empty);
        ListView listView = (ListView) inflate.findViewById(R.id.list_view);
        this.mLocaleList = listView;
        listView.setOnItemClickListener(this);
        ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
        this.mActionBar = appCompatActionBar;
        appCompatActionBar.setDisplayShowCustomEnabled(true);
        this.mActionBar.setDisplayShowTitleEnabled(false);
        this.mActionBar.setCustomView(R.layout.timezone_search_titlebar);
        View customView = this.mActionBar.getCustomView();
        EditText editText = (EditText) customView.findViewById(16908297);
        this.mSearchView = editText;
        editText.addTextChangedListener(new TextWatcher() { // from class: com.android.settings.locale.MiuiLocaleSettings.2
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String charSequence2 = charSequence.toString();
                if (charSequence2.equals(MiuiLocaleSettings.this.mSearchText)) {
                    return;
                }
                MiuiLocaleSettings.this.mSearchText = charSequence2;
                MiuiLocaleSettings miuiLocaleSettings = MiuiLocaleSettings.this;
                miuiLocaleSettings.onQueryTextSubmit(miuiLocaleSettings.mSearchText);
            }
        });
        View findViewById = customView.findViewById(16908332);
        findViewById.setVisibility(0);
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.locale.MiuiLocaleSettings.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MiuiLocaleSettings.this.finish();
            }
        });
        this.mInputMethodManager = (InputMethodManager) this.mContext.getSystemService("input_method");
        this.mLocaleSettingsHelper = LocaleSettingsHelper.getInstance();
        if (!Locale.getDefault().toString().equals(this.mLocaleSettingsHelper.getCurrentLocale())) {
            this.mLocaleSettingsHelper.setLocales(MiuiInit.getCustVariants());
        }
        if (RegionUtils.IS_MEXICO_TELCEL) {
            this.mLocaleSettingsHelper.setLocales(new String[]{"mx"});
        }
        this.mLocaleSettingsHelper.constructLocaleList(this.mContext);
        LocaleAdapter localeAdapter = new LocaleAdapter(this.mContext);
        this.mLocaleAdapter = localeAdapter;
        this.mLocaleList.setAdapter((ListAdapter) localeAdapter);
        AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() { // from class: com.android.settings.locale.MiuiLocaleSettings.4
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == 1) {
                    MiuiLocaleSettings.this.mInputMethodManager.hideSoftInputFromWindow(MiuiLocaleSettings.this.mSearchView.getWindowToken(), 0);
                }
            }
        };
        this.mOnListScrollListener = onScrollListener;
        this.mLocaleList.setOnScrollListener(onScrollListener);
        return inflate;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        String countryCode = ((LocaleInfo) this.mLocaleAdapter.getItem(i)).getCountryCode();
        if (countryCode.equalsIgnoreCase(this.mSelectedLocale)) {
            Log.d("MiuiLocaleSettings", "No need to set since same local chosen,selectedLocale=" + countryCode);
            return;
        }
        this.mSelectedLocale = countryCode;
        this.mLocaleAdapter.notifyDataSetChanged();
        if (!MiuiInit.initCustEnvironment(countryCode, (IMiuiInitObserver) null)) {
            Log.d("MiuiLocaleSettings", "Fail to call MiuiInit.initCustEnvironment, please retry.");
            finish();
            return;
        }
        String id = TimeZone.getDefault().getID();
        Intent intent = new Intent("android.intent.action.TIMEZONE_CHANGED");
        intent.addFlags(536870912);
        intent.putExtra("time-zone", id);
        this.mOnSetup = true;
        showInstallPreInstallAppDialog();
        getActivity().sendBroadcastAsUser(intent, UserHandle.ALL);
        AsyncTask.execute(new InstallAppTask(this));
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mHandler.removeMessages(1);
        ProgressDialog progressDialog = this.mDialog;
        if (progressDialog != null) {
            progressDialog.cancel();
            this.mDialog = null;
        }
    }

    public void onQueryTextSubmit(String str) {
        QueryAsyncTask queryAsyncTask = this.mQueryAsyncTask;
        if (queryAsyncTask != null) {
            queryAsyncTask.cancel(true);
        }
        QueryAsyncTask queryAsyncTask2 = new QueryAsyncTask(this, str);
        this.mQueryAsyncTask = queryAsyncTask2;
        queryAsyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("on_setup", this.mOnSetup);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        QueryAsyncTask queryAsyncTask = this.mQueryAsyncTask;
        if (queryAsyncTask != null) {
            queryAsyncTask.cancel(true);
            this.mQueryAsyncTask = null;
        }
        super.onStop();
    }
}
