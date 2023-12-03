package com.android.settings;

import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.users.UserDialogs;
import java.util.ArrayList;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class DeviceAdminAddFragment extends BaseFragment {
    Button mActionButton;
    TextView mAddMsg;
    ImageView mAddMsgExpander;
    CharSequence mAddMsgText;
    boolean mAdding;
    TextView mAdminDescription;
    ImageView mAdminIcon;
    TextView mAdminName;
    ViewGroup mAdminPolicies;
    TextView mAdminWarning;
    Button mCancelButton;
    DevicePolicyManager mDPM;
    DeviceAdminInfo mDeviceAdmin;
    private CharSequence mExtraDisableWarningMsg;
    Handler mHandler;
    boolean mRefreshing;
    boolean mAddMsgEllipsized = true;
    final ArrayList<View> mAddingPolicies = new ArrayList<>();
    final ArrayList<View> mActivePolicies = new ArrayList<>();

    private View getPermissionItemView(CharSequence charSequence, CharSequence charSequence2) {
        View inflate = ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(R.layout.app_permission_item_old, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.permission_group);
        TextView textView2 = (TextView) inflate.findViewById(R.id.permission_list);
        ((ImageView) inflate.findViewById(R.id.perm_icon)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_text_dot));
        if (charSequence != null) {
            textView.setText(charSequence);
            textView2.setText(charSequence2);
        } else {
            textView.setText(charSequence2);
            textView2.setVisibility(8);
        }
        return inflate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isManagedProfile(DeviceAdminInfo deviceAdminInfo) {
        UserInfo userInfo = UserManager.get(getActivity()).getUserInfo(UserHandle.getUserId(deviceAdminInfo.getActivityInfo().applicationInfo.uid));
        if (userInfo != null) {
            return userInfo.isManagedProfile();
        }
        return false;
    }

    static void setViewVisibility(ArrayList<View> arrayList, int i) {
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            arrayList.get(i2).setVisibility(i);
        }
    }

    @Override // com.android.settings.BaseFragment
    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.miui_device_admin_add, viewGroup, false);
    }

    int getEllipsizedLines() {
        Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        return defaultDisplay.getHeight() > defaultDisplay.getWidth() ? 5 : 2;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x009f, code lost:
    
        r9.activityInfo = r3;
        new android.app.admin.DeviceAdminInfo(getActivity(), r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x00aa, code lost:
    
        r0 = true;
     */
    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onCreate(android.os.Bundle r13) {
        /*
            Method dump skipped, instructions count: 418
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.DeviceAdminAddFragment.onCreate(android.os.Bundle):void");
    }

    @Override // com.android.settings.BaseFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 1) {
            return super.onCreateDialog(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(this.mExtraDisableWarningMsg);
        builder.setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.DeviceAdminAddFragment.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                DeviceAdminAddFragment deviceAdminAddFragment = DeviceAdminAddFragment.this;
                deviceAdminAddFragment.mDPM.removeActiveAdmin(deviceAdminAddFragment.mDeviceAdmin.getComponent());
                DeviceAdminAddFragment.this.finish();
            }
        });
        builder.setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateInterface();
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        this.mAdminIcon = (ImageView) view.findViewById(R.id.admin_icon);
        this.mAdminName = (TextView) view.findViewById(R.id.admin_name);
        this.mAdminDescription = (TextView) view.findViewById(R.id.admin_description);
        this.mAddMsg = (TextView) view.findViewById(R.id.add_msg);
        this.mAddMsgExpander = (ImageView) view.findViewById(R.id.add_msg_expander);
        this.mAddMsg.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.DeviceAdminAddFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                DeviceAdminAddFragment.this.toggleMessageEllipsis(view2);
            }
        });
        toggleMessageEllipsis(this.mAddMsg);
        this.mAdminWarning = (TextView) view.findViewById(R.id.admin_warning);
        this.mAdminPolicies = (ViewGroup) view.findViewById(R.id.admin_policies);
        Button button = (Button) view.findViewById(R.id.cancel_button);
        this.mCancelButton = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.DeviceAdminAddFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                DeviceAdminAddFragment.this.finish();
            }
        });
        Button button2 = (Button) view.findViewById(R.id.action_button);
        this.mActionButton = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.DeviceAdminAddFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                DeviceAdminAddFragment deviceAdminAddFragment = DeviceAdminAddFragment.this;
                if (!deviceAdminAddFragment.mAdding) {
                    if (deviceAdminAddFragment.isManagedProfile(deviceAdminAddFragment.mDeviceAdmin) && DeviceAdminAddFragment.this.mDeviceAdmin.getComponent().equals(DeviceAdminAddFragment.this.mDPM.getProfileOwner())) {
                        final int myUserId = UserHandle.myUserId();
                        UserDialogs.createRemoveDialog(DeviceAdminAddFragment.this.getActivity(), myUserId, new DialogInterface.OnClickListener() { // from class: com.android.settings.DeviceAdminAddFragment.3.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UserManager.get(DeviceAdminAddFragment.this.getActivity()).removeUser(myUserId);
                                DeviceAdminAddFragment.this.finish();
                            }
                        }).show();
                        return;
                    }
                    try {
                        ActivityManagerNative.getDefault().stopAppSwitches();
                    } catch (RemoteException unused) {
                    }
                    DeviceAdminAddFragment deviceAdminAddFragment2 = DeviceAdminAddFragment.this;
                    deviceAdminAddFragment2.mDPM.getRemoveWarning(deviceAdminAddFragment2.mDeviceAdmin.getComponent(), new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.settings.DeviceAdminAddFragment.3.2
                        public void onResult(Bundle bundle2) {
                            DeviceAdminAddFragment.this.mExtraDisableWarningMsg = bundle2 != null ? bundle2.getCharSequence("android.app.extra.DISABLE_WARNING") : null;
                            if (DeviceAdminAddFragment.this.mExtraDisableWarningMsg != null) {
                                DeviceAdminAddFragment.this.showDialog(1);
                                return;
                            }
                            try {
                                ActivityManagerNative.getDefault().resumeAppSwitches();
                            } catch (RemoteException unused2) {
                            }
                            DeviceAdminAddFragment deviceAdminAddFragment3 = DeviceAdminAddFragment.this;
                            deviceAdminAddFragment3.mDPM.removeActiveAdmin(deviceAdminAddFragment3.mDeviceAdmin.getComponent());
                            DeviceAdminAddFragment.this.finish();
                        }
                    }, DeviceAdminAddFragment.this.mHandler));
                    return;
                }
                try {
                    deviceAdminAddFragment.mDPM.setActiveAdmin(deviceAdminAddFragment.mDeviceAdmin.getComponent(), DeviceAdminAddFragment.this.mRefreshing);
                    DeviceAdminAddFragment.this.setResult(-1);
                } catch (RuntimeException e) {
                    Log.w("DeviceAdminAdd", "Exception trying to activate admin " + DeviceAdminAddFragment.this.mDeviceAdmin.getComponent(), e);
                    DeviceAdminAddFragment deviceAdminAddFragment3 = DeviceAdminAddFragment.this;
                    if (deviceAdminAddFragment3.mDPM.isAdminActive(deviceAdminAddFragment3.mDeviceAdmin.getComponent())) {
                        DeviceAdminAddFragment.this.setResult(-1);
                    }
                }
                DeviceAdminAddFragment.this.finish();
            }
        });
        super.onViewCreated(view, bundle);
    }

    void toggleMessageEllipsis(View view) {
        TextView textView = (TextView) view;
        boolean z = !this.mAddMsgEllipsized;
        this.mAddMsgEllipsized = z;
        textView.setEllipsize(z ? TextUtils.TruncateAt.END : null);
        textView.setMaxLines(this.mAddMsgEllipsized ? getEllipsizedLines() : 15);
        this.mAddMsgExpander.setImageResource(this.mAddMsgEllipsized ? 285736971 : 285736970);
    }

    void updateInterface() {
        this.mAdminIcon.setImageDrawable(this.mDeviceAdmin.loadIcon(getPackageManager()));
        CharSequence loadLabel = this.mDeviceAdmin.loadLabel(getPackageManager());
        this.mAdminName.setText(loadLabel);
        getActivity().getActionBar().setTitle(loadLabel);
        try {
            this.mAdminDescription.setText(this.mDeviceAdmin.loadDescription(getPackageManager()));
            this.mAdminDescription.setVisibility(0);
        } catch (Resources.NotFoundException unused) {
            this.mAdminDescription.setVisibility(8);
        }
        CharSequence charSequence = this.mAddMsgText;
        if (charSequence != null) {
            this.mAddMsg.setText(charSequence);
            this.mAddMsg.setVisibility(0);
        } else {
            this.mAddMsg.setVisibility(8);
            this.mAddMsgExpander.setVisibility(8);
        }
        if (this.mRefreshing || !this.mDPM.isAdminActive(this.mDeviceAdmin.getComponent())) {
            if (this.mAddingPolicies.size() == 0) {
                ArrayList usedPolicies = this.mDeviceAdmin.getUsedPolicies();
                for (int i = 0; i < usedPolicies.size(); i++) {
                    DeviceAdminInfo.PolicyInfo policyInfo = (DeviceAdminInfo.PolicyInfo) usedPolicies.get(i);
                    View permissionItemView = getPermissionItemView(getText(policyInfo.label), getText(policyInfo.description));
                    this.mAddingPolicies.add(permissionItemView);
                    this.mAdminPolicies.addView(permissionItemView);
                }
            }
            setViewVisibility(this.mAddingPolicies, 0);
            setViewVisibility(this.mActivePolicies, 8);
            this.mAdminWarning.setText(getString(R.string.device_admin_warning, this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager())));
            this.mActionButton.setText(getText(R.string.add_device_admin));
            this.mAdding = true;
            return;
        }
        if (this.mActivePolicies.size() == 0) {
            ArrayList usedPolicies2 = this.mDeviceAdmin.getUsedPolicies();
            for (int i2 = 0; i2 < usedPolicies2.size(); i2++) {
                View permissionItemView2 = getPermissionItemView(getText(((DeviceAdminInfo.PolicyInfo) usedPolicies2.get(i2)).label), "");
                this.mActivePolicies.add(permissionItemView2);
                this.mAdminPolicies.addView(permissionItemView2);
            }
        }
        setViewVisibility(this.mActivePolicies, 0);
        setViewVisibility(this.mAddingPolicies, 8);
        this.mAdminWarning.setText(getString(R.string.device_admin_status, this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager())));
        this.mActionButton.setText(getText(R.string.remove_device_admin));
        this.mAdding = false;
        boolean equals = this.mDeviceAdmin.getComponent().equals(this.mDPM.getProfileOwner());
        boolean isManagedProfile = isManagedProfile(this.mDeviceAdmin);
        if (equals && isManagedProfile) {
            this.mAdminWarning.setText(R.string.admin_profile_owner_message);
            this.mActionButton.setText(R.string.remove_managed_profile_label);
        }
    }
}
