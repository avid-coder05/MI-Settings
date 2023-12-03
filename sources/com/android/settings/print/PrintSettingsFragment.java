package com.android.settings.print;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintJob;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.printservice.PrintServiceInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import miui.os.Build;

/* loaded from: classes2.dex */
public class PrintSettingsFragment extends ProfileSettingsPreferenceFragment implements View.OnClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.print_settings);
    private PreferenceCategory mActivePrintJobsCategory;
    private Button mAddNewServiceButton;
    private Preference mPrintHelpPreference;
    private PrintJobsController mPrintJobsController;
    private PreferenceCategory mPrintServicesCategory;
    private PrintServicesController mPrintServicesController;

    /* loaded from: classes2.dex */
    private final class PrintJobsController implements LoaderManager.LoaderCallbacks<List<PrintJobInfo>> {
        private PrintJobsController() {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<PrintJobInfo>> onCreateLoader(int i, Bundle bundle) {
            if (i == 1) {
                return new PrintJobsLoader(PrintSettingsFragment.this.getContext());
            }
            return null;
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<List<PrintJobInfo>> loader, List<PrintJobInfo> list) {
            if (list == null || list.isEmpty()) {
                PrintSettingsFragment.this.getPreferenceScreen().removePreference(PrintSettingsFragment.this.mActivePrintJobsCategory);
                return;
            }
            if (PrintSettingsFragment.this.getPreferenceScreen().findPreference("print_jobs_category") == null) {
                PrintSettingsFragment.this.getPreferenceScreen().addPreference(PrintSettingsFragment.this.mActivePrintJobsCategory);
            }
            PrintSettingsFragment.this.mActivePrintJobsCategory.removeAll();
            Context prefContext = PrintSettingsFragment.this.getPrefContext();
            if (prefContext == null) {
                Log.w("PrintSettingsFragment", "No preference context, skip adding print jobs");
                return;
            }
            for (PrintJobInfo printJobInfo : list) {
                Preference preference = new Preference(prefContext);
                preference.setPersistent(false);
                preference.setFragment(PrintJobSettingsFragment.class.getName());
                preference.setKey(printJobInfo.getId().flattenToString());
                int state = printJobInfo.getState();
                if (state == 2 || state == 3) {
                    if (printJobInfo.isCancelling()) {
                        preference.setTitle(PrintSettingsFragment.this.getString(R.string.print_cancelling_state_title_template, printJobInfo.getLabel()));
                    } else {
                        preference.setTitle(PrintSettingsFragment.this.getString(R.string.print_printing_state_title_template, printJobInfo.getLabel()));
                    }
                } else if (state != 4) {
                    if (state == 6) {
                        preference.setTitle(PrintSettingsFragment.this.getString(R.string.print_failed_state_title_template, printJobInfo.getLabel()));
                    }
                } else if (printJobInfo.isCancelling()) {
                    preference.setTitle(PrintSettingsFragment.this.getString(R.string.print_cancelling_state_title_template, printJobInfo.getLabel()));
                } else {
                    preference.setTitle(PrintSettingsFragment.this.getString(R.string.print_blocked_state_title_template, printJobInfo.getLabel()));
                }
                preference.setSummary(PrintSettingsFragment.this.getString(R.string.print_job_summary, printJobInfo.getPrinterName(), DateUtils.formatSameDayTime(printJobInfo.getCreationTime(), printJobInfo.getCreationTime(), 3, 3)));
                TypedArray obtainStyledAttributes = PrintSettingsFragment.this.getActivity().obtainStyledAttributes(new int[]{16843817});
                obtainStyledAttributes.getColor(0, 0);
                obtainStyledAttributes.recycle();
                Drawable appLaunchIcon = AppInfoUtils.getAppLaunchIcon(prefContext, "com.android.bips");
                int state2 = printJobInfo.getState();
                if (state2 == 2 || state2 == 3) {
                    Drawable drawable = PrintSettingsFragment.this.getActivity().getDrawable(17302831);
                    if (appLaunchIcon == null) {
                        appLaunchIcon = drawable;
                    }
                    preference.setIcon(appLaunchIcon);
                } else if (state2 == 4 || state2 == 6) {
                    Drawable drawable2 = PrintSettingsFragment.this.getActivity().getDrawable(17302832);
                    if (appLaunchIcon == null) {
                        appLaunchIcon = drawable2;
                    }
                    preference.setIcon(appLaunchIcon);
                }
                preference.getExtras().putString("EXTRA_PRINT_JOB_ID", printJobInfo.getId().flattenToString());
                PrintSettingsFragment.this.mActivePrintJobsCategory.addPreference(preference);
            }
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<PrintJobInfo>> loader) {
            PrintSettingsFragment.this.getPreferenceScreen().removePreference(PrintSettingsFragment.this.mActivePrintJobsCategory);
        }
    }

    /* loaded from: classes2.dex */
    private static final class PrintJobsLoader extends AsyncTaskLoader<List<PrintJobInfo>> {
        private PrintManager.PrintJobStateChangeListener mPrintJobStateChangeListener;
        private List<PrintJobInfo> mPrintJobs;
        private final PrintManager mPrintManager;

        public PrintJobsLoader(Context context) {
            super(context);
            this.mPrintJobs = new ArrayList();
            this.mPrintManager = ((PrintManager) context.getSystemService("print")).getGlobalPrintManagerForUser(context.getUserId());
        }

        @Override // androidx.loader.content.Loader
        public void deliverResult(List<PrintJobInfo> list) {
            if (isStarted()) {
                super.deliverResult((PrintJobsLoader) list);
            }
        }

        @Override // androidx.loader.content.AsyncTaskLoader
        public List<PrintJobInfo> loadInBackground() {
            List<PrintJob> printJobs = this.mPrintManager.getPrintJobs();
            int size = printJobs.size();
            ArrayList arrayList = null;
            for (int i = 0; i < size; i++) {
                PrintJobInfo info = printJobs.get(i).getInfo();
                if (PrintSettingPreferenceController.shouldShowToUser(info)) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(info);
                }
            }
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onReset() {
            onStopLoading();
            this.mPrintJobs.clear();
            PrintManager.PrintJobStateChangeListener printJobStateChangeListener = this.mPrintJobStateChangeListener;
            if (printJobStateChangeListener != null) {
                this.mPrintManager.removePrintJobStateChangeListener(printJobStateChangeListener);
                this.mPrintJobStateChangeListener = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onStartLoading() {
            if (!this.mPrintJobs.isEmpty()) {
                deliverResult((List<PrintJobInfo>) new ArrayList(this.mPrintJobs));
            }
            if (this.mPrintJobStateChangeListener == null) {
                PrintManager.PrintJobStateChangeListener printJobStateChangeListener = new PrintManager.PrintJobStateChangeListener() { // from class: com.android.settings.print.PrintSettingsFragment.PrintJobsLoader.1
                    public void onPrintJobStateChanged(PrintJobId printJobId) {
                        PrintJobsLoader.this.onForceLoad();
                    }
                };
                this.mPrintJobStateChangeListener = printJobStateChangeListener;
                this.mPrintManager.addPrintJobStateChangeListener(printJobStateChangeListener);
            }
            if (this.mPrintJobs.isEmpty()) {
                onForceLoad();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onStopLoading() {
            onCancelLoad();
        }
    }

    /* loaded from: classes2.dex */
    private final class PrintServicesController implements LoaderManager.LoaderCallbacks<List<PrintServiceInfo>> {
        private PrintServicesController() {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<PrintServiceInfo>> onCreateLoader(int i, Bundle bundle) {
            PrintManager printManager = (PrintManager) PrintSettingsFragment.this.getContext().getSystemService("print");
            if (printManager != null) {
                return new SettingsPrintServicesLoader(printManager, PrintSettingsFragment.this.getContext(), 3);
            }
            return null;
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<List<PrintServiceInfo>> loader, List<PrintServiceInfo> list) {
            if (list.isEmpty()) {
                PrintSettingsFragment.this.getPreferenceScreen().removePreference(PrintSettingsFragment.this.mPrintServicesCategory);
                return;
            }
            if (PrintSettingsFragment.this.getPreferenceScreen().findPreference("print_services_category") == null) {
                PrintSettingsFragment.this.getPreferenceScreen().addPreference(PrintSettingsFragment.this.mPrintServicesCategory);
            }
            PrintSettingsFragment.this.mPrintServicesCategory.removeAll();
            PackageManager packageManager = PrintSettingsFragment.this.getActivity().getPackageManager();
            Context prefContext = PrintSettingsFragment.this.getPrefContext();
            if (prefContext == null) {
                Log.w("PrintSettingsFragment", "No preference context, skip adding print services");
                return;
            }
            for (PrintServiceInfo printServiceInfo : list) {
                ValuePreference valuePreference = new ValuePreference(prefContext);
                valuePreference.setLayoutResource(R.layout.preference_print_services);
                valuePreference.setShowRightArrow(true);
                String charSequence = printServiceInfo.getResolveInfo().loadLabel(packageManager).toString();
                valuePreference.setTitle(charSequence);
                ComponentName componentName = printServiceInfo.getComponentName();
                valuePreference.setKey(componentName.flattenToString());
                valuePreference.setFragment(PrintServiceSettingsFragment.class.getName());
                valuePreference.setPersistent(false);
                if ("com.android.bips/com.android.bips.BuiltInPrintService".equals(componentName.flattenToString())) {
                    valuePreference.setOrder(-1);
                }
                if (printServiceInfo.isEnabled()) {
                    valuePreference.setValue(PrintSettingsFragment.this.getString(R.string.print_feature_state_on));
                } else {
                    valuePreference.setValue(PrintSettingsFragment.this.getString(R.string.print_feature_state_off));
                }
                Drawable loadIcon = printServiceInfo.getResolveInfo().loadIcon(packageManager);
                if (loadIcon != null) {
                    valuePreference.setIcon(loadIcon);
                }
                Bundle extras = valuePreference.getExtras();
                extras.putBoolean("EXTRA_CHECKED", printServiceInfo.isEnabled());
                extras.putString("EXTRA_TITLE", charSequence);
                extras.putString("EXTRA_SERVICE_COMPONENT_NAME", componentName.flattenToString());
                PrintSettingsFragment.this.mPrintServicesCategory.addPreference(valuePreference);
            }
            Preference newAddServicePreferenceOrNull = PrintSettingsFragment.this.newAddServicePreferenceOrNull();
            if (newAddServicePreferenceOrNull == null || !Build.IS_INTERNATIONAL_BUILD) {
                return;
            }
            PrintSettingsFragment.this.mPrintServicesCategory.addPreference(newAddServicePreferenceOrNull);
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<PrintServiceInfo>> loader) {
            PrintSettingsFragment.this.getPreferenceScreen().removePreference(PrintSettingsFragment.this.mPrintServicesCategory);
        }
    }

    private Intent createAddNewServiceIntentOrNull() {
        String string = Settings.Secure.getString(getContentResolver(), "print_service_search_uri");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return new Intent("android.intent.action.VIEW", Uri.parse(string));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreateView$0(Preference preference) {
        getPrefContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(("http://api.comm.miui.com/miprint/" + Locale.getDefault().toString().toLowerCase() + "/help.html").replace("#", ""))));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Preference newAddServicePreferenceOrNull() {
        Intent createAddNewServiceIntentOrNull = createAddNewServiceIntentOrNull();
        if (createAddNewServiceIntentOrNull == null) {
            return null;
        }
        Preference preference = new Preference(getPrefContext());
        preference.setTitle(R.string.print_menu_item_add_service);
        preference.setIcon(R.drawable.ic_add_24dp);
        preference.setOrder(2147483646);
        preference.setIntent(createAddNewServiceIntentOrNull);
        preference.setPersistent(false);
        return preference;
    }

    private void startSubSettingsIfNeeded() {
        String string;
        if (getArguments() == null || (string = getArguments().getString("EXTRA_PRINT_SERVICE_COMPONENT_NAME")) == null) {
            return;
        }
        getArguments().remove("EXTRA_PRINT_SERVICE_COMPONENT_NAME");
        Preference findPreference = findPreference(string);
        if (findPreference != null) {
            findPreference.performClick();
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_uri_printing;
    }

    @Override // com.android.settings.print.ProfileSettingsPreferenceFragment
    protected String getIntentActionString() {
        return "android.settings.ACTION_PRINT_SETTINGS";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 80;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Intent createAddNewServiceIntentOrNull;
        if (this.mAddNewServiceButton != view || (createAddNewServiceIntentOrNull = createAddNewServiceIntentOrNull()) == null) {
            return;
        }
        try {
            startActivity(createAddNewServiceIntentOrNull);
        } catch (ActivityNotFoundException e) {
            Log.w("PrintSettingsFragment", "Unable to start activity", e);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.print_settings_preference_list_fragment, viewGroup, false);
        ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
        addPreferencesFromResource(R.xml.print_settings);
        this.mActivePrintJobsCategory = (PreferenceCategory) findPreference("print_jobs_category");
        this.mPrintServicesCategory = (PreferenceCategory) findPreference("print_services_category");
        getPreferenceScreen().removePreference(this.mActivePrintJobsCategory);
        this.mPrintJobsController = new PrintJobsController();
        getLoaderManager().initLoader(1, null, this.mPrintJobsController);
        this.mPrintServicesController = new PrintServicesController();
        getLoaderManager().initLoader(2, null, this.mPrintServicesController);
        Preference findPreference = findPreference("print_help");
        this.mPrintHelpPreference = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.print.PrintSettingsFragment$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$onCreateView$0;
                    lambda$onCreateView$0 = PrintSettingsFragment.this.lambda$onCreateView$0(preference);
                    return lambda$onCreateView$0;
                }
            });
        }
        return inflate;
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().removeAll();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        startSubSettingsIfNeeded();
    }

    @Override // com.android.settings.print.ProfileSettingsPreferenceFragment, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        this.mPinnedHeaderFrameLayout = (ViewGroup) view.findViewById(R.id.pinned_header);
        super.onViewCreated(view, bundle);
        ViewGroup viewGroup = (ViewGroup) getListView().getParent();
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.empty_print_state, viewGroup, false);
        ((TextView) inflate.findViewById(R.id.message)).setText(R.string.print_no_services_installed);
        if (createAddNewServiceIntentOrNull() != null) {
            Button button = (Button) inflate.findViewById(R.id.add_new_service);
            this.mAddNewServiceButton = button;
            button.setOnClickListener(this);
            this.mAddNewServiceButton.setVisibility(0);
        }
        viewGroup.addView(inflate);
        setEmptyView(inflate);
    }
}
