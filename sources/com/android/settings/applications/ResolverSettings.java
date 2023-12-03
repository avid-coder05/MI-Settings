package com.android.settings.applications;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.MiuiSettings;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.BaseFragment;
import com.android.settings.R;
import com.miui.maml.util.AppIconsHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class ResolverSettings extends BaseFragment {
    private ResolverListAdapter mAdapter;
    private List<ComponentName> mComponentOrders;
    private int mIconSize;
    protected boolean mOrderChanged = false;
    private Map<ComponentName, Integer> mOrderMap;
    private List<ResolveInfo> mOriginResolveList;
    private SparseArray<ResolveInfo> mResolveArray;

    /* loaded from: classes.dex */
    private class ResolverListAdapter extends BaseAdapter {
        private SparseArray<ResolveInfo> mArray;

        private ResolverListAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mArray.size();
        }

        @Override // android.widget.Adapter
        public ResolveInfo getItem(int i) {
            if (i < 0 || i >= this.mArray.size()) {
                return null;
            }
            SparseArray<ResolveInfo> sparseArray = this.mArray;
            return sparseArray.get(sparseArray.keyAt(i));
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            if (getItem(i) != null) {
                return r0.hashCode();
            }
            return -1L;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(ResolverSettings.this.getActivity()).inflate(R.layout.resolver_settings_item, viewGroup, false);
                ViewHolder viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
                ViewGroup.LayoutParams layoutParams = viewHolder.icon.getLayoutParams();
                int i2 = ResolverSettings.this.mIconSize;
                layoutParams.height = i2;
                layoutParams.width = i2;
            }
            ResolveInfo item = getItem(i);
            if (item != null) {
                ViewHolder viewHolder2 = (ViewHolder) view.getTag();
                PackageManager packageManager = ResolverSettings.this.getPackageManager();
                viewHolder2.text.setText(item.loadLabel(packageManager));
                viewHolder2.icon.setImageDrawable(AppIconsHelper.getIconDrawable(ResolverSettings.this.getActivity(), item, packageManager, 120000L));
                viewHolder2.drag.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.applications.ResolverSettings.ResolverListAdapter.1
                    @Override // android.view.View.OnTouchListener
                    public boolean onTouch(View view2, MotionEvent motionEvent) {
                        motionEvent.getActionMasked();
                        return false;
                    }
                });
            }
            return view;
        }

        public void setArray(SparseArray<ResolveInfo> sparseArray) {
            this.mArray = sparseArray;
            notifyDataSetChanged();
        }
    }

    /* loaded from: classes.dex */
    private static class ViewHolder {
        public View drag;
        public ImageView icon;
        public TextView text;

        public ViewHolder(View view) {
            this.text = (TextView) view.findViewById(16908308);
            this.icon = (ImageView) view.findViewById(16908294);
            this.drag = view.findViewById(R.id.drag);
        }
    }

    private ComponentName buildComponent(ResolveInfo resolveInfo) {
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        return new ComponentName(activityInfo.packageName, activityInfo.name);
    }

    private void updateMapAndArray() {
        this.mOrderMap = new HashMap();
        for (int i = 0; i < this.mComponentOrders.size(); i++) {
            this.mOrderMap.put(this.mComponentOrders.get(i), Integer.valueOf(i));
        }
        this.mResolveArray = new SparseArray<>();
        for (ResolveInfo resolveInfo : this.mOriginResolveList) {
            ComponentName buildComponent = buildComponent(resolveInfo);
            if (this.mOrderMap.containsKey(buildComponent)) {
                this.mResolveArray.put(this.mOrderMap.get(buildComponent).intValue(), resolveInfo);
            } else {
                int size = this.mComponentOrders.size();
                this.mResolveArray.put(size, resolveInfo);
                this.mOrderMap.put(buildComponent, Integer.valueOf(size));
                this.mComponentOrders.add(buildComponent);
                this.mOrderChanged = true;
            }
        }
    }

    @Override // com.android.settings.BaseFragment
    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.resolver_settings, viewGroup, false);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ArrayList parcelableArrayListExtra = getActivity().getIntent().getParcelableArrayListExtra("rlist");
        this.mOriginResolveList = parcelableArrayListExtra;
        if (parcelableArrayListExtra == null) {
            this.mOriginResolveList = new ArrayList();
        }
        this.mComponentOrders = MiuiSettings.System.getActivityResolveOrder(getActivity().getContentResolver());
        updateMapAndArray();
        ResolverListAdapter resolverListAdapter = new ResolverListAdapter();
        this.mAdapter = resolverListAdapter;
        resolverListAdapter.setArray(this.mResolveArray);
        this.mIconSize = ((ActivityManager) getActivity().getSystemService("activity")).getLauncherLargeIconSize();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (this.mOrderChanged) {
            MiuiSettings.System.putActivityResolveOrder(getActivity().getContentResolver(), this.mComponentOrders);
        }
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }
}
