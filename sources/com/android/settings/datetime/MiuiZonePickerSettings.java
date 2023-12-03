package com.android.settings.datetime;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.settings.BaseFragment;
import com.android.settings.R;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miui.app.constants.ThemeManagerConstants;
import miuix.appcompat.app.ActionBar;

/* loaded from: classes.dex */
public class MiuiZonePickerSettings extends BaseFragment implements AdapterView.OnItemClickListener {
    private ActionBar mActionBar;
    private Context mContext;
    private InputMethodManager mImm;
    private AbsListView.OnScrollListener mOnListScrollListener;
    private String mSearchText;
    private EditText mSearchView;
    private TextWatcher mTextWatcher = new TextWatcher() { // from class: com.android.settings.datetime.MiuiZonePickerSettings.3
        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            MiuiZonePickerSettings.this.mSearchText = charSequence.toString();
            MiuiZonePickerSettings miuiZonePickerSettings = MiuiZonePickerSettings.this;
            miuiZonePickerSettings.onQueryTextSubmit(miuiZonePickerSettings.mSearchText);
        }
    };
    private ListView mTimeZoneList;
    private TimezoneAdapter mTimezoneAdapter;
    private ZonePickerHelper mZonePickerHelper;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TimezoneAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<TimeZoneObj> mTimezones;

        public TimezoneAdapter() {
            this.mInflater = LayoutInflater.from(MiuiZonePickerSettings.this.mContext);
            this.mTimezones = MiuiZonePickerSettings.this.mZonePickerHelper.queryTimezoneItems("");
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mTimezones.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mTimezones.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r2v2, types: [android.text.SpannableStringBuilder, java.lang.CharSequence] */
        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.mInflater.inflate(R.layout.timezone_search_item, viewGroup, false);
            }
            TimeZoneObj timeZoneObj = (TimeZoneObj) getItem(i);
            String cityName = timeZoneObj.getCityName();
            MiuiZonePickerSettings miuiZonePickerSettings = MiuiZonePickerSettings.this;
            ?? highlight = miuiZonePickerSettings.highlight(cityName, miuiZonePickerSettings.mSearchText);
            TextView textView = (TextView) view.findViewById(R.id.text1);
            if (!TextUtils.isEmpty(highlight)) {
                cityName = highlight;
            }
            textView.setText(cityName);
            ((TextView) view.findViewById(R.id.text2)).setText(timeZoneObj.getGmtName());
            return view;
        }

        public void setData(List<TimeZoneObj> list) {
            this.mTimezones = list;
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

    private void onSortModeChange(int i) {
        this.mZonePickerHelper.setSortMode(i);
        onQueryTextSubmit(this.mSearchText);
    }

    @Override // com.android.settings.BaseFragment
    public void finish() {
        this.mImm.hideSoftInputFromWindow(this.mSearchView.getWindowToken(), 0);
        if (isResumed()) {
            getActivity().onBackPressed();
        } else {
            getActivity().finish();
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        this.mContext = getContext();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.timezone_sort_mode, menu);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.timezone_search, viewGroup, false);
        ListView listView = (ListView) inflate.findViewById(R.id.list_view);
        this.mTimeZoneList = listView;
        listView.setOnItemClickListener(this);
        ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
        this.mActionBar = appCompatActionBar;
        appCompatActionBar.setCustomView(R.layout.timezone_search_titlebar);
        this.mActionBar.setDisplayShowCustomEnabled(true);
        this.mActionBar.setDisplayShowTitleEnabled(false);
        View customView = this.mActionBar.getCustomView();
        EditText editText = (EditText) customView.findViewById(16908297);
        this.mSearchView = editText;
        editText.addTextChangedListener(this.mTextWatcher);
        View findViewById = customView.findViewById(16908332);
        findViewById.setVisibility(0);
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.datetime.MiuiZonePickerSettings.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MiuiZonePickerSettings.this.finish();
            }
        });
        this.mImm = (InputMethodManager) this.mContext.getSystemService("input_method");
        this.mZonePickerHelper = new ZonePickerHelper(this.mContext.getApplicationContext());
        TimezoneAdapter timezoneAdapter = new TimezoneAdapter();
        this.mTimezoneAdapter = timezoneAdapter;
        this.mTimeZoneList.setAdapter((ListAdapter) timezoneAdapter);
        this.mOnListScrollListener = new AbsListView.OnScrollListener() { // from class: com.android.settings.datetime.MiuiZonePickerSettings.2
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == 1) {
                    MiuiZonePickerSettings.this.mImm.hideSoftInputFromWindow(MiuiZonePickerSettings.this.mSearchView.getWindowToken(), 0);
                }
            }
        };
        onSortModeChange(this.mZonePickerHelper.getSortMode());
        return inflate;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        TimeZoneObj timeZoneObj = (TimeZoneObj) this.mTimezoneAdapter.getItem(i);
        ((AlarmManager) this.mContext.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM)).setTimeZone(timeZoneObj.getID());
        Log.i("MiuiZonePickerSettings", "Timezone changed: " + timeZoneObj.toString());
        finish();
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.sort_by_timezone) {
            onSortModeChange(1);
        } else if (itemId == R.id.sort_by_alphabet) {
            onSortModeChange(0);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onQueryTextSubmit(String str) {
        this.mTimezoneAdapter.setData(this.mZonePickerHelper.queryTimezoneItems(str));
        this.mTimezoneAdapter.notifyDataSetChanged();
    }
}
