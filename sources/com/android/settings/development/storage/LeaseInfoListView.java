package com.android.settings.development.storage;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.blob.BlobInfo;
import android.app.blob.BlobStoreManager;
import android.app.blob.LeaseInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.internal.util.CollectionUtils;
import com.android.settings.R;
import java.io.IOException;
import java.util.List;

/* loaded from: classes.dex */
public class LeaseInfoListView extends ListActivity {
    private LeaseListAdapter mAdapter;
    private BlobInfo mBlobInfo;
    private BlobStoreManager mBlobStoreManager;
    private Context mContext;
    private LayoutInflater mInflater;

    /* loaded from: classes.dex */
    private class LeaseListAdapter extends ArrayAdapter<LeaseInfo> {
        private Context mContext;

        LeaseListAdapter(Context context) {
            super(context, 0);
            this.mContext = context;
            List leases = LeaseInfoListView.this.mBlobInfo.getLeases();
            if (CollectionUtils.isEmpty(leases)) {
                return;
            }
            addAll(leases);
        }

        private String getDescriptionString(LeaseInfo leaseInfo) {
            try {
                try {
                    String string = LeaseInfoListView.this.getString(leaseInfo.getDescriptionResId());
                    if (!TextUtils.isEmpty(string)) {
                        return string;
                    }
                } catch (Resources.NotFoundException unused) {
                    String charSequence = leaseInfo.getDescription() != null ? leaseInfo.getDescription().toString() : null;
                    if (!TextUtils.isEmpty(charSequence)) {
                        return charSequence;
                    }
                }
                return LeaseInfoListView.this.getString(R.string.accessor_no_description_text);
            } catch (Throwable th) {
                if (TextUtils.isEmpty(null)) {
                    LeaseInfoListView.this.getString(R.string.accessor_no_description_text);
                }
                throw th;
            }
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            Drawable drawable;
            LeaseInfoViewHolder createOrRecycle = LeaseInfoViewHolder.createOrRecycle(LeaseInfoListView.this.mInflater, view);
            View view2 = createOrRecycle.rootView;
            view2.setEnabled(false);
            LeaseInfo item = getItem(i);
            try {
                drawable = this.mContext.getPackageManager().getApplicationIcon(item.getPackageName());
            } catch (PackageManager.NameNotFoundException unused) {
                drawable = this.mContext.getDrawable(17301651);
            }
            createOrRecycle.appIcon.setImageDrawable(drawable);
            createOrRecycle.leasePackageName.setText(item.getPackageName());
            createOrRecycle.leaseDescription.setText(getDescriptionString(item));
            createOrRecycle.leaseExpiry.setText(LeaseInfoListView.this.getString(R.string.accessor_expires_text, new Object[]{SharedDataUtils.formatTime(item.getExpiryTimeMillis())}));
            return view2;
        }
    }

    private View.OnClickListener getButtonOnClickListener() {
        return new View.OnClickListener() { // from class: com.android.settings.development.storage.LeaseInfoListView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LeaseInfoListView.this.lambda$getButtonOnClickListener$0(view);
            }
        };
    }

    private DialogInterface.OnClickListener getDialogOnClickListener() {
        return new DialogInterface.OnClickListener() { // from class: com.android.settings.development.storage.LeaseInfoListView$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LeaseInfoListView.this.lambda$getDialogOnClickListener$1(dialogInterface, i);
            }
        };
    }

    private Button getFooterView() {
        Button button = new Button(this);
        button.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        button.setText(R.string.delete_blob_text);
        button.setOnClickListener(getButtonOnClickListener());
        return button;
    }

    private LinearLayout getHeaderView() {
        LinearLayout linearLayout = (LinearLayout) this.mInflater.inflate(R.layout.blob_list_item_view, (ViewGroup) null);
        linearLayout.setEnabled(false);
        TextView textView = (TextView) linearLayout.findViewById(R.id.blob_label);
        TextView textView2 = (TextView) linearLayout.findViewById(R.id.blob_id);
        TextView textView3 = (TextView) linearLayout.findViewById(R.id.blob_expiry);
        TextView textView4 = (TextView) linearLayout.findViewById(R.id.blob_size);
        textView.setText(this.mBlobInfo.getLabel());
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView2.setText(getString(R.string.blob_id_text, new Object[]{Long.valueOf(this.mBlobInfo.getId())}));
        textView3.setVisibility(8);
        textView4.setText(SharedDataUtils.formatSize(this.mBlobInfo.getSizeBytes()));
        return linearLayout;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getButtonOnClickListener$0(View view) {
        new AlertDialog.Builder(this.mContext).setMessage(R.string.delete_blob_confirmation_text).setPositiveButton(17039370, getDialogOnClickListener()).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDialogOnClickListener$1(DialogInterface dialogInterface, int i) {
        try {
            this.mBlobStoreManager.deleteBlob(this.mBlobInfo);
            setResult(1);
        } catch (IOException e) {
            Log.e("LeaseInfoListView", "Unable to delete blob: " + e.getMessage());
            setResult(-1);
        }
        finish();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = this;
        this.mBlobStoreManager = (BlobStoreManager) getSystemService(BlobStoreManager.class);
        this.mInflater = (LayoutInflater) getSystemService(LayoutInflater.class);
        this.mBlobInfo = getIntent().getParcelableExtra("BLOB_KEY");
        LeaseListAdapter leaseListAdapter = new LeaseListAdapter(this);
        this.mAdapter = leaseListAdapter;
        if (leaseListAdapter.isEmpty()) {
            Log.e("LeaseInfoListView", "Error fetching leases for shared data: " + this.mBlobInfo.toString());
            finish();
        }
        setListAdapter(this.mAdapter);
        getListView().addHeaderView(getHeaderView());
        getListView().addFooterView(getFooterView());
        getListView().setClickable(false);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override // android.app.Activity
    public boolean onNavigateUp() {
        finish();
        return true;
    }
}
