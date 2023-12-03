package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.applications.RunningProcessesView;
import com.android.settings.applications.RunningState;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Collections;
import miui.content.ExtraIntent;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class RunningServiceDetails extends InstrumentedFragment implements RunningState.OnRefreshUiListener {
    ViewGroup mAllDetails;
    ActivityManager mAm;
    boolean mHaveData;
    LayoutInflater mInflater;
    RunningState.MergedItem mMergedItem;
    int mNumProcesses;
    int mNumServices;
    String mProcessName;
    TextView mProcessesHeader;
    View mRootView;
    TextView mServicesHeader;
    boolean mShowBackground;
    ViewGroup mSnippet;
    RunningProcessesView.ActiveItem mSnippetActiveItem;
    RunningProcessesView.ViewHolder mSnippetViewHolder;
    RunningState mState;
    int mUid;
    int mUserId;
    final ArrayList<ActiveDetail> mActiveDetails = new ArrayList<>();
    StringBuilder mBuilder = new StringBuilder(128);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ActiveDetail implements View.OnClickListener {
        RunningProcessesView.ActiveItem mActiveItem;
        ComponentName mInstaller;
        PendingIntent mManageIntent;
        Button mReportButton;
        View mRootView;
        RunningState.ServiceItem mServiceItem;
        Button mStopButton;
        RunningProcessesView.ViewHolder mViewHolder;

        ActiveDetail() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:26:0x00bc, code lost:
        
            if (r7 == null) goto L74;
         */
        /* JADX WARN: Removed duplicated region for block: B:80:0x013a A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // android.view.View.OnClickListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onClick(android.view.View r13) {
            /*
                Method dump skipped, instructions count: 409
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.RunningServiceDetails.ActiveDetail.onClick(android.view.View):void");
        }

        void stopActiveService(boolean z) {
            RunningState.ServiceItem serviceItem = this.mServiceItem;
            if (!z && (serviceItem.mServiceInfo.applicationInfo.flags & 1) != 0) {
                RunningServiceDetails.this.showConfirmStopDialog(serviceItem.mRunningService.service);
                return;
            }
            RunningServiceDetails.this.getActivity().stopServiceAsUser(new Intent().setComponent(serviceItem.mRunningService.service), new UserHandle(RunningServiceDetails.this.mMergedItem.mUserId));
            RunningServiceDetails runningServiceDetails = RunningServiceDetails.this;
            RunningState.MergedItem mergedItem = runningServiceDetails.mMergedItem;
            if (mergedItem == null) {
                runningServiceDetails.mState.updateNow();
                RunningServiceDetails.this.finish();
            } else if (runningServiceDetails.mShowBackground || mergedItem.mServices.size() > 1) {
                RunningServiceDetails.this.mState.updateNow();
            } else {
                RunningServiceDetails.this.mState.updateNow();
                RunningServiceDetails.this.finish();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class MyAlertDialogFragment extends InstrumentedDialogFragment {
        public static MyAlertDialogFragment newConfirmStop(int i, ComponentName componentName) {
            MyAlertDialogFragment myAlertDialogFragment = new MyAlertDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id", i);
            bundle.putParcelable("comp", componentName);
            myAlertDialogFragment.setArguments(bundle);
            return myAlertDialogFragment;
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 536;
        }

        RunningServiceDetails getOwner() {
            return (RunningServiceDetails) getTargetFragment();
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            int i = getArguments().getInt("id");
            if (i == 1) {
                final ComponentName componentName = (ComponentName) getArguments().getParcelable("comp");
                if (getOwner().activeDetailForService(componentName) == null) {
                    return null;
                }
                return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getString(R.string.runningservicedetails_stop_dlg_title)).setMessage(getActivity().getString(R.string.runningservicedetails_stop_dlg_text)).setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.RunningServiceDetails.MyAlertDialogFragment.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        ActiveDetail activeDetailForService = MyAlertDialogFragment.this.getOwner().activeDetailForService(componentName);
                        if (activeDetailForService != null) {
                            activeDetailForService.stopActiveService(true);
                        }
                    }
                }).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).create();
            }
            throw new IllegalArgumentException("unknown id " + i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finish() {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.applications.RunningServiceDetails$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RunningServiceDetails.this.lambda$finish$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finish$0() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.onBackPressed();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showConfirmStopDialog(ComponentName componentName) {
        MyAlertDialogFragment newConfirmStop = MyAlertDialogFragment.newConfirmStop(1, componentName);
        newConfirmStop.setTargetFragment(this, 0);
        try {
            newConfirmStop.show(getFragmentManager(), "confirmstop");
        } catch (IllegalStateException e) {
            Log.e("RunningServicesDetails", "Failed to show confirm stop dialog", e);
        }
    }

    ActiveDetail activeDetailForService(ComponentName componentName) {
        ActivityManager.RunningServiceInfo runningServiceInfo;
        for (int i = 0; i < this.mActiveDetails.size(); i++) {
            ActiveDetail activeDetail = this.mActiveDetails.get(i);
            RunningState.ServiceItem serviceItem = activeDetail.mServiceItem;
            if (serviceItem != null && (runningServiceInfo = serviceItem.mRunningService) != null && componentName.equals(runningServiceInfo.service)) {
                return activeDetail;
            }
        }
        return null;
    }

    void addDetailViews() {
        ArrayList<RunningState.MergedItem> arrayList;
        for (int size = this.mActiveDetails.size() - 1; size >= 0; size--) {
            this.mAllDetails.removeView(this.mActiveDetails.get(size).mRootView);
        }
        this.mActiveDetails.clear();
        TextView textView = this.mServicesHeader;
        if (textView != null) {
            this.mAllDetails.removeView(textView);
            this.mServicesHeader = null;
        }
        TextView textView2 = this.mProcessesHeader;
        if (textView2 != null) {
            this.mAllDetails.removeView(textView2);
            this.mProcessesHeader = null;
        }
        this.mNumProcesses = 0;
        this.mNumServices = 0;
        RunningState.MergedItem mergedItem = this.mMergedItem;
        if (mergedItem != null) {
            if (mergedItem.mUser == null) {
                addDetailsViews(mergedItem, true, true);
                return;
            }
            if (this.mShowBackground) {
                arrayList = new ArrayList<>(this.mMergedItem.mChildren);
                Collections.sort(arrayList, this.mState.mBackgroundComparator);
            } else {
                arrayList = mergedItem.mChildren;
            }
            for (int i = 0; i < arrayList.size(); i++) {
                addDetailsViews(arrayList.get(i), true, false);
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                addDetailsViews(arrayList.get(i2), false, true);
            }
        }
    }

    void addDetailsViews(RunningState.MergedItem mergedItem, boolean z, boolean z2) {
        if (mergedItem != null) {
            if (z) {
                for (int i = 0; i < mergedItem.mServices.size(); i++) {
                    addServiceDetailsView(mergedItem.mServices.get(i), mergedItem, true, true);
                }
            }
            if (z2) {
                if (mergedItem.mServices.size() <= 0) {
                    addServiceDetailsView(null, mergedItem, false, (mergedItem.mUserId == UserHandle.myUserId() || mergedItem.mUserId == 999) ? false : true);
                    return;
                }
                int i2 = -1;
                while (i2 < mergedItem.mOtherProcesses.size()) {
                    RunningState.ProcessItem processItem = i2 < 0 ? mergedItem.mProcess : mergedItem.mOtherProcesses.get(i2);
                    if (processItem == null || processItem.mPid > 0) {
                        addProcessDetailsView(processItem, i2 < 0);
                    }
                    i2++;
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x00a1 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void addProcessDetailsView(com.android.settings.applications.RunningState.ProcessItem r8, boolean r9) {
        /*
            r7 = this;
            r7.addProcessesHeader()
            com.android.settings.applications.RunningServiceDetails$ActiveDetail r0 = new com.android.settings.applications.RunningServiceDetails$ActiveDetail
            r0.<init>()
            android.view.LayoutInflater r1 = r7.mInflater
            int r2 = com.android.settings.R.layout.running_service_details_process
            android.view.ViewGroup r3 = r7.mAllDetails
            r4 = 0
            android.view.View r1 = r1.inflate(r2, r3, r4)
            android.view.ViewGroup r2 = r7.mAllDetails
            r2.addView(r1)
            r0.mRootView = r1
            com.android.settings.applications.RunningProcessesView$ViewHolder r2 = new com.android.settings.applications.RunningProcessesView$ViewHolder
            r2.<init>(r1)
            r0.mViewHolder = r2
            com.android.settings.applications.RunningState r3 = r7.mState
            java.lang.StringBuilder r5 = r7.mBuilder
            com.android.settings.applications.RunningProcessesView$ActiveItem r2 = r2.bind(r3, r8, r5)
            r0.mActiveItem = r2
            int r2 = com.android.settings.R.id.comp_description
            android.view.View r1 = r1.findViewById(r2)
            android.widget.TextView r1 = (android.widget.TextView) r1
            int r2 = r8.mUserId
            int r3 = android.os.UserHandle.myUserId()
            if (r2 == r3) goto L47
            int r2 = r8.mUserId
            r3 = 999(0x3e7, float:1.4E-42)
            if (r2 == r3) goto L47
            r8 = 8
            r1.setVisibility(r8)
            goto Lb2
        L47:
            if (r9 == 0) goto L4f
            int r8 = com.android.settings.R.string.main_running_process_description
            r1.setText(r8)
            goto Lb2
        L4f:
            r9 = 0
            android.app.ActivityManager$RunningAppProcessInfo r8 = r8.mRunningProcessInfo
            android.content.ComponentName r2 = r8.importanceReasonComponent
            int r3 = r8.importanceReasonCode
            r5 = 1
            if (r3 == r5) goto L7f
            r6 = 2
            if (r3 == r6) goto L5e
            r3 = r4
            goto L9f
        L5e:
            int r3 = com.android.settings.R.string.process_service_in_use_description
            if (r2 == 0) goto L9f
            androidx.fragment.app.FragmentActivity r2 = r7.getActivity()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            android.content.pm.PackageManager r2 = r2.getPackageManager()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            android.content.ComponentName r8 = r8.importanceReasonComponent     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            android.content.pm.ServiceInfo r8 = r2.getServiceInfo(r8, r4)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            androidx.fragment.app.FragmentActivity r2 = r7.getActivity()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            android.content.pm.PackageManager r2 = r2.getPackageManager()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            java.lang.String r6 = r8.name     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            java.lang.CharSequence r9 = com.android.settings.applications.RunningState.makeLabel(r2, r6, r8)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            goto L9f
        L7f:
            int r3 = com.android.settings.R.string.process_provider_in_use_description
            if (r2 == 0) goto L9f
            androidx.fragment.app.FragmentActivity r2 = r7.getActivity()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            android.content.pm.PackageManager r2 = r2.getPackageManager()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            android.content.ComponentName r8 = r8.importanceReasonComponent     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            android.content.pm.ProviderInfo r8 = r2.getProviderInfo(r8, r4)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            androidx.fragment.app.FragmentActivity r2 = r7.getActivity()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            android.content.pm.PackageManager r2 = r2.getPackageManager()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            java.lang.String r6 = r8.name     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
            java.lang.CharSequence r9 = com.android.settings.applications.RunningState.makeLabel(r2, r6, r8)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L9f
        L9f:
            if (r3 == 0) goto Lb2
            if (r9 == 0) goto Lb2
            androidx.fragment.app.FragmentActivity r8 = r7.getActivity()
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r9
            java.lang.String r8 = r8.getString(r3, r2)
            r1.setText(r8)
        Lb2:
            java.util.ArrayList<com.android.settings.applications.RunningServiceDetails$ActiveDetail> r7 = r7.mActiveDetails
            r7.add(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.RunningServiceDetails.addProcessDetailsView(com.android.settings.applications.RunningState$ProcessItem, boolean):void");
    }

    void addProcessesHeader() {
        if (this.mNumProcesses == 0) {
            TextView textView = (TextView) this.mInflater.inflate(R.layout.preference_category, this.mAllDetails, false);
            this.mProcessesHeader = textView;
            textView.setText(R.string.runningservicedetails_processes_title);
            this.mAllDetails.addView(this.mProcessesHeader);
        }
        this.mNumProcesses++;
    }

    /* JADX WARN: Multi-variable type inference failed */
    void addServiceDetailsView(RunningState.ServiceItem serviceItem, RunningState.MergedItem mergedItem, boolean z, boolean z2) {
        if (z) {
            addServicesHeader();
        } else if (mergedItem.mUserId != UserHandle.myUserId() && mergedItem.mUserId != 999) {
            addProcessesHeader();
        }
        RunningState.ServiceItem serviceItem2 = serviceItem != null ? serviceItem : mergedItem;
        ActiveDetail activeDetail = new ActiveDetail();
        View inflate = this.mInflater.inflate(R.layout.running_service_details_service, this.mAllDetails, false);
        this.mAllDetails.addView(inflate);
        activeDetail.mRootView = inflate;
        activeDetail.mServiceItem = serviceItem;
        RunningProcessesView.ViewHolder viewHolder = new RunningProcessesView.ViewHolder(inflate);
        activeDetail.mViewHolder = viewHolder;
        activeDetail.mActiveItem = viewHolder.bind(this.mState, serviceItem2, this.mBuilder);
        if (!z2) {
            inflate.findViewById(R.id.service).setVisibility(8);
        }
        if (serviceItem != null) {
            ActivityManager.RunningServiceInfo runningServiceInfo = serviceItem.mRunningService;
            if (runningServiceInfo.clientLabel != 0) {
                activeDetail.mManageIntent = this.mAm.getRunningServiceControlPanel(runningServiceInfo.service);
            }
        }
        TextView textView = (TextView) inflate.findViewById(R.id.comp_description);
        activeDetail.mStopButton = (Button) inflate.findViewById(R.id.left_button);
        activeDetail.mReportButton = (Button) inflate.findViewById(R.id.right_button);
        if (!z || mergedItem.mUserId == UserHandle.myUserId() || mergedItem.mUserId == 999) {
            if (serviceItem != null && serviceItem.mServiceInfo.descriptionRes != 0) {
                PackageManager packageManager = getActivity().getPackageManager();
                ServiceInfo serviceInfo = serviceItem.mServiceInfo;
                textView.setText(packageManager.getText(serviceInfo.packageName, serviceInfo.descriptionRes, serviceInfo.applicationInfo));
            } else if (mergedItem.mBackground) {
                textView.setText(R.string.background_process_stop_description);
            } else if (serviceItem == null || activeDetail.mManageIntent == null) {
                textView.setText(getActivity().getText(serviceItem != null ? R.string.service_stop_description : R.string.heavy_weight_stop_description));
            } else {
                try {
                    textView.setText(getActivity().getString(R.string.service_manage_description, new Object[]{getActivity().getPackageManager().getResourcesForApplication(serviceItem.mRunningService.clientPackage).getString(serviceItem.mRunningService.clientLabel)}));
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
            activeDetail.mStopButton.setOnClickListener(activeDetail);
            activeDetail.mStopButton.setText(getActivity().getText(activeDetail.mManageIntent != null ? R.string.service_manage : R.string.service_stop));
            activeDetail.mReportButton.setOnClickListener(activeDetail);
            activeDetail.mReportButton.setText(286195753);
            if (Settings.Global.getInt(getActivity().getContentResolver(), "send_action_app_error", 0) == 0 || serviceItem == null) {
                activeDetail.mReportButton.setEnabled(false);
            } else {
                FragmentActivity activity = getActivity();
                ServiceInfo serviceInfo2 = serviceItem.mServiceInfo;
                ComponentName errorReportReceiver = ApplicationErrorReport.getErrorReportReceiver(activity, serviceInfo2.packageName, serviceInfo2.applicationInfo.flags);
                activeDetail.mInstaller = errorReportReceiver;
                activeDetail.mReportButton.setEnabled(errorReportReceiver != null);
            }
        } else {
            textView.setVisibility(8);
            inflate.findViewById(R.id.control_buttons_panel).setVisibility(8);
        }
        this.mActiveDetails.add(activeDetail);
    }

    void addServicesHeader() {
        if (this.mNumServices == 0) {
            TextView textView = (TextView) this.mInflater.inflate(R.layout.preference_category, this.mAllDetails, false);
            this.mServicesHeader = textView;
            textView.setText(R.string.runningservicedetails_services_title);
            this.mAllDetails.addView(this.mServicesHeader);
        }
        this.mNumServices++;
    }

    void ensureData() {
        if (this.mHaveData) {
            return;
        }
        this.mHaveData = true;
        this.mState.resume(this);
        this.mState.waitForData();
        refreshUi(true);
    }

    boolean findMergedItem() {
        RunningState.MergedItem mergedItem;
        int i;
        String str;
        RunningState.ProcessItem processItem;
        RunningState.ProcessItem processItem2;
        ArrayList<RunningState.MergedItem> currentBackgroundItems = this.mShowBackground ? this.mState.getCurrentBackgroundItems() : this.mState.getCurrentMergedItems();
        if (currentBackgroundItems != null) {
            for (int i2 = 0; i2 < currentBackgroundItems.size(); i2++) {
                mergedItem = currentBackgroundItems.get(i2);
                if (mergedItem.mUserId == this.mUserId && (((i = this.mUid) < 0 || (processItem2 = mergedItem.mProcess) == null || processItem2.mUid == i) && ((str = this.mProcessName) == null || ((processItem = mergedItem.mProcess) != null && str.equals(processItem.mProcessName))))) {
                    break;
                }
            }
        }
        mergedItem = null;
        if (this.mMergedItem != mergedItem) {
            this.mMergedItem = mergedItem;
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 85;
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        this.mUid = getArguments().getInt("uid", -1);
        this.mUserId = getArguments().getInt(ExtraIntent.EXTRA_XIAOMI_ACCOUNT_USER_ID, 0);
        this.mProcessName = getArguments().getString("process", null);
        this.mShowBackground = getArguments().getBoolean("background", false);
        this.mAm = (ActivityManager) getActivity().getSystemService("activity");
        this.mInflater = (LayoutInflater) getActivity().getSystemService("layout_inflater");
        this.mState = RunningState.getInstance(getActivity());
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.running_service_details, viewGroup, false);
        Utils.prepareCustomPreferencesList(viewGroup, inflate, inflate, false);
        this.mRootView = inflate;
        this.mAllDetails = (ViewGroup) inflate.findViewById(R.id.all_details);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.snippet);
        this.mSnippet = viewGroup2;
        this.mSnippetViewHolder = new RunningProcessesView.ViewHolder(viewGroup2);
        ensureData();
        return inflate;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mHaveData = false;
        this.mState.pause();
    }

    @Override // com.android.settings.applications.RunningState.OnRefreshUiListener
    public void onRefreshUi(int i) {
        if (getActivity() == null) {
            return;
        }
        if (i == 0) {
            updateTimes();
        } else if (i == 1) {
            refreshUi(false);
            updateTimes();
        } else if (i != 2) {
        } else {
            refreshUi(true);
            updateTimes();
        }
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        ensureData();
    }

    void refreshUi(boolean z) {
        if (findMergedItem()) {
            z = true;
        }
        if (z) {
            RunningState.MergedItem mergedItem = this.mMergedItem;
            if (mergedItem != null) {
                this.mSnippetActiveItem = this.mSnippetViewHolder.bind(this.mState, mergedItem, this.mBuilder);
            } else {
                RunningProcessesView.ActiveItem activeItem = this.mSnippetActiveItem;
                if (activeItem == null) {
                    finish();
                    return;
                }
                activeItem.mHolder.size.setText("");
                this.mSnippetActiveItem.mHolder.uptime.setText("");
                this.mSnippetActiveItem.mHolder.description.setText(R.string.no_services);
            }
            addDetailViews();
        }
    }

    void updateTimes() {
        RunningProcessesView.ActiveItem activeItem = this.mSnippetActiveItem;
        if (activeItem != null) {
            activeItem.updateTime(getActivity(), this.mBuilder);
        }
        for (int i = 0; i < this.mActiveDetails.size(); i++) {
            this.mActiveDetails.get(i).mActiveItem.updateTime(getActivity(), this.mBuilder);
        }
    }
}
