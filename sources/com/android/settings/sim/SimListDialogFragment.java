package com.android.settings.sim;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.network.SubscriptionUtil;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.List;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class SimListDialogFragment extends SimDialogFragment implements DialogInterface.OnClickListener {
    protected SelectSubscriptionAdapter mAdapter;
    List<SubscriptionInfo> mSubscriptions;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class SelectSubscriptionAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        List<SubscriptionInfo> mSubscriptions;

        public SelectSubscriptionAdapter(Context context, List<SubscriptionInfo> list) {
            this.mSubscriptions = list;
            this.mContext = context;
        }

        private boolean isMdnProvisioned(String str) {
            return (TextUtils.isEmpty(str) || str.matches("[\\D0]+")) ? false : true;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mSubscriptions.size();
        }

        @Override // android.widget.Adapter
        public SubscriptionInfo getItem(int i) {
            return this.mSubscriptions.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            if (this.mSubscriptions.get(i) == null) {
                return -1L;
            }
            return r0.getSubscriptionId();
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                if (this.mInflater == null) {
                    this.mInflater = LayoutInflater.from(viewGroup.getContext());
                }
                view = this.mInflater.inflate(R.layout.select_account_list_item, viewGroup, false);
            }
            SubscriptionInfo item = getItem(i);
            TextView textView = (TextView) view.findViewById(R.id.title);
            TextView textView2 = (TextView) view.findViewById(R.id.summary);
            ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            if (item == null) {
                textView.setText(R.string.sim_calls_ask_first_prefs_title);
                textView2.setText("");
                imageView.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_feedback_24dp));
                imageView.setImageTintList(Utils.getColorAttr(this.mContext, 16842808));
            } else {
                textView.setText(SubscriptionUtil.getUniqueSubscriptionDisplayName(item, this.mContext));
                textView2.setText(isMdnProvisioned(item.getNumber()) ? item.getNumber() : "");
                imageView.setImageBitmap(item.createIconBitmap(this.mContext));
            }
            return view;
        }
    }

    public static SimListDialogFragment newInstance(int i, int i2, boolean z) {
        SimListDialogFragment simListDialogFragment = new SimListDialogFragment();
        Bundle initArguments = SimDialogFragment.initArguments(i, i2);
        initArguments.putBoolean("include_ask_every_time", z);
        simListDialogFragment.setArguments(initArguments);
        return simListDialogFragment;
    }

    protected List<SubscriptionInfo> getCurrentSubscriptions() {
        return ((SubscriptionManager) getContext().getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoList();
    }

    public int getMetricsCategory() {
        return 1707;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i < 0 || i >= this.mSubscriptions.size()) {
            return;
        }
        SubscriptionInfo subscriptionInfo = this.mSubscriptions.get(i);
        ((SimDialogActivity) getActivity()).onSubscriptionSelected(getDialogType(), subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        this.mSubscriptions = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getTitleResId());
        this.mAdapter = new SelectSubscriptionAdapter(builder.getContext(), this.mSubscriptions);
        setAdapter(builder);
        AlertDialog create = builder.create();
        updateDialog();
        return create;
    }

    void setAdapter(AlertDialog.Builder builder) {
        builder.setAdapter(this.mAdapter, this);
    }

    @Override // com.android.settings.sim.SimDialogFragment
    public void updateDialog() {
        Log.d("SimListDialogFragment", "Dialog updated, dismiss status: " + this.mWasDismissed);
        List<SubscriptionInfo> currentSubscriptions = getCurrentSubscriptions();
        if (currentSubscriptions == null) {
            if (this.mWasDismissed) {
                return;
            }
            dismiss();
            return;
        }
        if (getArguments().getBoolean("include_ask_every_time")) {
            ArrayList arrayList = new ArrayList(currentSubscriptions.size() + 1);
            arrayList.add(null);
            arrayList.addAll(currentSubscriptions);
            currentSubscriptions = arrayList;
        }
        if (currentSubscriptions.equals(this.mSubscriptions)) {
            return;
        }
        this.mSubscriptions.clear();
        this.mSubscriptions.addAll(currentSubscriptions);
        this.mAdapter.notifyDataSetChanged();
    }
}
