package com.android.settings.fuelgauge;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.fuelgauge.PowerAllowlistBackend;
import miuix.appcompat.app.AlertDialog;
import miuix.util.Log;

/* loaded from: classes.dex */
public class HighPowerDetail extends InstrumentedDialogFragment implements DialogInterface.OnClickListener, View.OnClickListener {
    PowerAllowlistBackend mBackend;
    BatteryUtils mBatteryUtils;
    private boolean mDefaultOn;
    boolean mIsEnabled;
    private CharSequence mLabel;
    private Checkable mOptionOff;
    private Checkable mOptionOn;
    String mPackageName;
    int mPackageUid;

    private View createIgnoreOptimizationsContentView() {
        View inflate = ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(R.layout.ignore_optimizations_content, (ViewGroup) null);
        this.mOptionOn = setup(inflate.findViewById(R.id.ignore_on), true);
        this.mOptionOff = setup(inflate.findViewById(R.id.ignore_off), false);
        return inflate;
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        return getSummary(context, appEntry.info.packageName);
    }

    static CharSequence getSummary(Context context, PowerAllowlistBackend powerAllowlistBackend, String str) {
        return context.getString((powerAllowlistBackend.isSysAllowlisted(str) || powerAllowlistBackend.isDefaultActiveApp(str)) ? R.string.high_power_system : powerAllowlistBackend.isAllowlisted(str) ? R.string.high_power_on : R.string.high_power_off);
    }

    public static CharSequence getSummary(Context context, String str) {
        return getSummary(context, PowerAllowlistBackend.getInstance(context), str);
    }

    static void logSpecialPermissionChange(boolean z, String str, Context context) {
        FeatureFactory.getFactory(context).getMetricsFeatureProvider().action(context, z ? 765 : 764, str);
    }

    public static void show(Fragment fragment, int i, String str, int i2) {
        HighPowerDetail highPowerDetail = new HighPowerDetail();
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putInt("uid", i);
        highPowerDetail.setArguments(bundle);
        highPowerDetail.setTargetFragment(fragment, i2);
        FragmentManager fragmentManager = fragment.getFragmentManager();
        if (fragmentManager == null || fragmentManager.isStateSaved() || fragmentManager.isDestroyed()) {
            return;
        }
        try {
            highPowerDetail.show(fragment.getFragmentManager(), HighPowerDetail.class.getSimpleName());
        } catch (Exception e) {
            Log.e(HighPowerDetail.class.getSimpleName(), "show error " + e.getMessage());
        }
    }

    private void updateViews() {
        this.mOptionOn.setChecked(this.mIsEnabled);
        this.mOptionOff.setChecked(!this.mIsEnabled);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 540;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            boolean z = this.mIsEnabled;
            if (z != this.mBackend.isAllowlisted(this.mPackageName)) {
                logSpecialPermissionChange(z, this.mPackageName, getContext());
                if (z) {
                    this.mBatteryUtils.setForceAppStandby(this.mPackageUid, this.mPackageName, 0);
                    this.mBackend.addApp(this.mPackageName);
                } else {
                    this.mBackend.removeApp(this.mPackageName);
                }
            }
            Fragment targetFragment = getTargetFragment();
            if (targetFragment != null) {
                targetFragment.onActivityResult(getTargetRequestCode(), 0, null);
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mOptionOn) {
            this.mIsEnabled = true;
            updateViews();
        } else if (view == this.mOptionOff) {
            this.mIsEnabled = false;
            updateViews();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mBatteryUtils = BatteryUtils.getInstance(context);
        this.mBackend = PowerAllowlistBackend.getInstance(context);
        this.mPackageName = getArguments().getString(FunctionColumns.PACKAGE);
        this.mPackageUid = getArguments().getInt("uid");
        PackageManager packageManager = context.getPackageManager();
        try {
            this.mLabel = packageManager.getApplicationInfo(this.mPackageName, 0).loadLabel(packageManager);
        } catch (PackageManager.NameNotFoundException unused) {
            this.mLabel = this.mPackageName;
        }
        boolean z = getArguments().getBoolean("default_on");
        this.mDefaultOn = z;
        this.mIsEnabled = z || this.mBackend.isAllowlisted(this.mPackageName);
        if (bundle == null || !bundle.containsKey("key_enabled")) {
            return;
        }
        this.mIsEnabled = bundle.getBoolean("key_enabled");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder view = new AlertDialog.Builder(getContext()).setTitle(this.mLabel).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setView(createIgnoreOptimizationsContentView());
        if (!this.mBackend.isSysAllowlisted(this.mPackageName)) {
            view.setPositiveButton(R.string.done, this);
        }
        return view.create();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateViews();
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("key_enabled", this.mIsEnabled);
    }

    public Checkable setup(View view, boolean z) {
        ((TextView) view.findViewById(16908310)).setText(z ? R.string.ignore_optimizations_on : R.string.ignore_optimizations_off);
        ((TextView) view.findViewById(16908304)).setText(z ? R.string.ignore_optimizations_on_desc : R.string.ignore_optimizations_off_desc);
        view.setClickable(true);
        view.setOnClickListener(this);
        if (!z && this.mBackend.isSysAllowlisted(this.mPackageName)) {
            view.setEnabled(false);
        }
        return (Checkable) view;
    }
}
